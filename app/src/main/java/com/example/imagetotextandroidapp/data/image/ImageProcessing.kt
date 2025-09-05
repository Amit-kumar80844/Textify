package com.example.imagetotextandroidapp.data.image

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.photo.Photo
import kotlin.math.abs
import kotlin.math.min
import androidx.core.graphics.createBitmap
import jakarta.inject.Inject
import java.io.Closeable
import kotlin.math.sqrt

/**
 * A utility class for preprocessing images for Optical Character Recognition (OCR).
 * This class applies a series of steps including grayscale conversion, resolution optimization,
 * noise reduction, contrast enhancement, deskewing, and binarization to improve OCR accuracy.
 */
class ImagePreProcessor @Inject constructor() {
    /**
     * The main function to preprocess a bitmap for OCR.
     *
     * @param inputBitmap The original bitmap to process.
     * @param targetDPI The desired resolution in DPI. Recommended values are 150, 300, or 600.
     * @param enableDeskewing A flag to enable or disable the deskewing step.
     * @return A new, preprocessed bitmap ready for OCR.
     */
    fun preprocessImageForOCR(
        inputBitmap: Bitmap,
        targetDPI: Int = 300,
        enableDeskewing: Boolean = true
    ): Bitmap {
        // Ensure bitmap is safe for OpenCV
        val safeBitmap = inputBitmap.copy(Bitmap.Config.ARGB_8888, true)

        val originalMat = Mat()
        try {
            // Use the safe copy instead of inputBitmap
            Utils.bitmapToMat(safeBitmap, originalMat)

            val grayMat = Mat()
            val resizedMat = Mat()
            val denoisedMat = Mat()
            val enhancedMat = Mat()
            val deskewedMat = Mat()
            val binaryMat = Mat()
            val resultBitmap: Bitmap

            try {
                // Step 1: Grayscale
                if (originalMat.channels() > 1) {
                    Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY)
                } else {
                    originalMat.copyTo(grayMat)
                }

                // Step 2: Resize
                optimizeResolution(grayMat, targetDPI).copyTo(resizedMat)

                // Step 3: Noise Reduction
                reduceNoise(resizedMat).copyTo(denoisedMat)

                // Step 4: Contrast/Brightness
                enhanceContrastAndBrightness(denoisedMat).copyTo(enhancedMat)

                // Step 5: Deskew (optional)
                if (enableDeskewing) {
                    correctSkew(enhancedMat).copyTo(deskewedMat)
                } else {
                    enhancedMat.copyTo(deskewedMat)
                }

                // Step 6: Binarization
                binarizeImage(deskewedMat).copyTo(binaryMat)

                // Convert back to Bitmap
                resultBitmap = createBitmap(binaryMat.cols(), binaryMat.rows())
                Utils.matToBitmap(binaryMat, resultBitmap)

            } finally {
                // Release intermediate Mat objects
                grayMat.release()
                resizedMat.release()
                denoisedMat.release()
                enhancedMat.release()
                deskewedMat.release()
                binaryMat.release()
            }

            return resultBitmap

        } finally {
            // Release the initial Mat
            originalMat.release()
        }
    }

    /**
     * Optimizes the image resolution for OCR.
     *
     * @param mat The input Mat.
     * @param targetDPI The target DPI.
     * @return The resized Mat.
     */
    private fun optimizeResolution(mat: Mat, targetDPI: Int): Mat {
        val currentWidth = mat.width()
        val currentHeight = mat.height()

        val minDimension = min(currentWidth, currentHeight)
        val targetMinSize = when (targetDPI) {
            150 -> 600
            300 -> 1200
            600 -> 2400
            else -> (targetDPI * 4) // A fallback for non-standard DPIs
        }

        val scaleFactor = when {
            minDimension < targetMinSize -> targetMinSize.toDouble() / minDimension
            else -> 1.0
        }

        if (scaleFactor != 1.0) {
            val newSize = Size(
                (currentWidth * scaleFactor).toInt().toDouble(),
                (currentHeight * scaleFactor).toInt().toDouble()
            )
            val resized = Mat()
            Imgproc.resize(mat, resized, newSize, 0.0, 0.0, Imgproc.INTER_CUBIC)
            return resized
        } else {
            return mat.clone()
        }
    }

    /**
     * Reduces noise in the image using a combination of Gaussian, Median, and Non-local Means filtering.
     *
     * @param mat The input Mat.
     * @return The denoised Mat.
     */
    private fun reduceNoise(mat: Mat): Mat {
        val tempMat1 = Mat()
        val tempMat2 = Mat()
        val gray8 = Mat()
        val result = Mat()

        try {
            // Combine Gaussian and Median blur for initial smoothing
            Imgproc.GaussianBlur(mat, tempMat1, Size(3.0, 3.0), 0.0)
            Imgproc.medianBlur(tempMat1, tempMat2, 3)

            // Ensure the image is 8-bit grayscale for fastNlMeansDenoising
            if (tempMat2.type() != CvType.CV_8UC1) {
                tempMat2.convertTo(gray8, CvType.CV_8UC1)
            } else {
                tempMat2.copyTo(gray8)
            }

            // Apply fastNlMeansDenoising for more aggressive noise reduction
            Photo.fastNlMeansDenoising(gray8, result, 10f, 7, 21)
            return result
        } finally {
            tempMat1.release()
            tempMat2.release()
            gray8.release()
        }
    }

    /**
     * Enhances the contrast and brightness of the image.
     *
     * @param mat The input Mat.
     * @return The enhanced Mat.
     */
    private fun enhanceContrastAndBrightness(mat: Mat): Mat {
        val mean = MatOfDouble()
        val std = MatOfDouble()
        val result = Mat()

        try {
            Core.meanStdDev(mat, mean, std)
            val meanValue = mean.get(0, 0)[0]
            val stdValue = std.get(0, 0)[0]

            // Heuristics for contrast and brightness
            val alpha = if (stdValue < 50) 1.5 else 1.2
            val beta = if (meanValue < 127) 20.0 else -10.0

            mat.convertTo(result, -1, alpha, beta)
            return result
        } finally {
            mean.release()
            std.release()
        }
    }

    /**
     * Corrects the skew (rotation) of the image.
     *
     * @param mat The input Mat.
     * @return The deskewed Mat.
     */
    private fun correctSkew(mat: Mat): Mat {
        val edges = Mat()
        val lines = Mat()

        try {
            Imgproc.Canny(mat, edges, 50.0, 150.0, 3, false)
            Imgproc.HoughLines(edges, lines, 1.0, Math.PI / 180, 100)

            var angleSum = 0.0
            var validLines = 0

            for (i in 0 until lines.rows()) {
                val line = lines.get(i, 0)
                val theta = line[1]
                val angleDeg = theta * 180.0 / Math.PI
                val skewAngle = angleDeg - 90.0

                // Filter for lines that are close to horizontal
                if (abs(skewAngle) < 45.0) {
                    angleSum += skewAngle
                    validLines++
                }
            }

            if (validLines > 0) {
                val avgAngle = angleSum / validLines
                if (abs(avgAngle) > 0.5) {
                    val center = Point(mat.width() / 2.0, mat.height() / 2.0)
                    val rotationMatrix = Imgproc.getRotationMatrix2D(center, avgAngle, 1.0)
                    val result = Mat()

                    try {
                        Imgproc.warpAffine(
                            mat, result, rotationMatrix,
                            Size(mat.width().toDouble(), mat.height().toDouble()),
                            Imgproc.INTER_CUBIC,
                            Core.BORDER_REPLICATE
                        )
                        return result
                    } finally {
                        rotationMatrix.release()
                    }
                }
            }
            return mat.clone()
        } finally {
            edges.release()
            lines.release()
        }
    }

    /**
     * Binarizes the image using either Adaptive Gaussian Thresholding or Otsu's method,
     * choosing the one that produces a more balanced black-to-white ratio.
     *
     * @param mat The input Mat.
     * @return The binarized Mat.
     */
    private fun binarizeImage(mat: Mat): Mat {
        val binaryAdaptive = Mat()
        val binaryOtsu = Mat()

        try {
            Imgproc.adaptiveThreshold(
                mat, binaryAdaptive,
                255.0,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY,
                15,
                10.0
            )

            Imgproc.threshold(mat, binaryOtsu, 0.0, 255.0, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)

            val totalPixels = mat.total().toDouble()
            if (totalPixels == 0.0) {
                return mat.clone()
            }

            val blackAdaptive = totalPixels - Core.countNonZero(binaryAdaptive).toDouble()
            val blackOtsu = totalPixels - Core.countNonZero(binaryOtsu).toDouble()

            val ratioAdaptive = blackAdaptive / totalPixels
            val ratioOtsu = blackOtsu / totalPixels

            // Heuristic: Choose the method that results in a black-to-white ratio closer to 15%
            // This prevents the image from being completely black or completely white, which can
            // happen with noisy or low-contrast inputs.
            return if (abs(ratioAdaptive - 0.15) < abs(ratioOtsu - 0.15)) {
                binaryAdaptive.clone()
            } else {
                binaryOtsu.clone()
            }
        } finally {
            binaryAdaptive.release()
            binaryOtsu.release()
        }
    }
}