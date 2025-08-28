package com.example.imagetotextandroidapp.data.image

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.photo.Photo
import kotlin.math.abs
import kotlin.math.min
import androidx.core.graphics.createBitmap

class ImagePreprocessor {

    fun preprocessImageForOCR(
        inputBitmap: Bitmap,
        targetDPI: Int = 300,
        enableDeskewing: Boolean = true
    ): Bitmap {
        val originalMat = Mat()
        Utils.bitmapToMat(inputBitmap, originalMat)

        // Step 1: Grayscale
        val grayMat = if (originalMat.channels() > 1) {
            val g = Mat()
            Imgproc.cvtColor(originalMat, g, Imgproc.COLOR_BGR2GRAY)
            g
        } else {
            originalMat.clone()
        }

        // Step 2: Resize
        val resizedMat = optimizeResolution(grayMat, targetDPI)

        // Step 3: Noise Reduction
        val denoisedMat = reduceNoise(resizedMat)

        // Step 4: Contrast/Brightness
        val enhancedMat = enhanceContrastAndBrightness(denoisedMat)

        // Step 5: Deskew (optional)
        val deskewedMat = if (enableDeskewing) correctSkew(enhancedMat) else enhancedMat

        // Step 6: Binarization
        val binaryMat = binarizeImage(deskewedMat)

        // Convert back to Bitmap
        val resultBitmap = createBitmap(binaryMat.cols(), binaryMat.rows())
        Utils.matToBitmap(binaryMat, resultBitmap)

        // Cleanup
        originalMat.release()
        grayMat.release()
        resizedMat.release()
        denoisedMat.release()
        enhancedMat.release()
        deskewedMat.release()
        binaryMat.release()

        return resultBitmap
    }

    private fun optimizeResolution(mat: Mat, targetDPI: Int): Mat {
        val currentWidth = mat.width()
        val currentHeight = mat.height()

        val minDimension = min(currentWidth, currentHeight)
        val targetMinSize = when (targetDPI) {
            150 -> 600
            300 -> 1200
            600 -> 2400
            else -> (targetDPI * 4)
        }

        val scaleFactor = when {
            minDimension < targetMinSize -> targetMinSize.toDouble() / minDimension
            minDimension > targetMinSize * 2 -> targetMinSize.toDouble() / minDimension
            else -> 1.0
        }

        return if (scaleFactor != 1.0) {
            val newSize = Size(
                (currentWidth * scaleFactor).toInt().toDouble(),
                (currentHeight * scaleFactor).toInt().toDouble()
            )
            val resized = Mat()
            Imgproc.resize(mat, resized, newSize, 0.0, 0.0, Imgproc.INTER_CUBIC)
            resized
        } else {
            mat.clone()
        }
    }

    private fun reduceNoise(mat: Mat): Mat {
        val temp1 = Mat()
        val temp2 = Mat()
        val result = Mat()

        // Gaussian + Median
        Imgproc.GaussianBlur(mat, temp1, Size(3.0, 3.0), 0.0)
        Imgproc.medianBlur(temp1, temp2, 3)

        // Ensure 8-bit grayscale
        var gray8 = Mat()
        if (temp2.type() != CvType.CV_8UC1) {
            temp2.convertTo(gray8, CvType.CV_8UC1)
        } else {
            gray8 = temp2.clone()
        }

        Photo.fastNlMeansDenoising(gray8, result, 10f, 7, 21)

        temp1.release()
        temp2.release()
        gray8.release()

        return result
    }

    private fun enhanceContrastAndBrightness(mat: Mat): Mat {
        val result = Mat()
        val mean = MatOfDouble()
        val std = MatOfDouble()
        Core.meanStdDev(mat, mean, std)

        val meanValue = mean.get(0, 0)[0]
        val stdValue = std.get(0, 0)[0]

        val alpha = if (stdValue < 50) 1.5 else 1.2
        val beta = if (meanValue < 127) 20.0 else -10.0

        mat.convertTo(result, -1, alpha, beta)

        mean.release()
        std.release()

        return result
    }

    private fun correctSkew(mat: Mat): Mat {
        val edges = Mat()
        val lines = Mat()

        Imgproc.Canny(mat, edges, 50.0, 150.0, 3, false)
        Imgproc.HoughLines(edges, lines, 1.0, Math.PI / 180, 100)

        var angleSum = 0.0
        var validLines = 0

        for (i in 0 until lines.rows()) {
            val line = lines.get(i, 0)
            val theta = line[1]
            val angleDeg = theta * 180.0 / Math.PI
            if (abs(angleDeg - 90.0) < 45.0) {
                angleSum += angleDeg - 90.0
                validLines++
            }
        }

        var result: Mat
        if (validLines > 0) {
            val avgAngle = angleSum / validLines
            if (abs(avgAngle) > 0.5) {
                val center = Point(mat.width() / 2.0, mat.height() / 2.0)
                val rotationMatrix = Imgproc.getRotationMatrix2D(center, avgAngle, 1.0)
                result = Mat()
                Imgproc.warpAffine(
                    mat, result, rotationMatrix,
                    Size(mat.width().toDouble(), mat.height().toDouble()),
                    Imgproc.INTER_CUBIC,
                    Core.BORDER_REPLICATE
                )
                rotationMatrix.release()
            } else {
                result = mat.clone()
            }
        } else {
            result = mat.clone()
        }

        edges.release()
        lines.release()
        return result
    }

    private fun binarizeImage(mat: Mat): Mat {
        val binary1 = Mat()
        val binary2 = Mat()

        Imgproc.adaptiveThreshold(
            mat, binary1,
            255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY,
            15,
            10.0
        )

        Imgproc.threshold(mat, binary2, 0.0, 255.0, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)

        val totalPixels = mat.total().toDouble()

        val black1 = totalPixels - Core.countNonZero(binary1).toDouble()
        val black2 = totalPixels - Core.countNonZero(binary2).toDouble()

        val ratio1 = black1 / totalPixels
        val ratio2 = black2 / totalPixels

        val result = if (abs(ratio1 - 0.15) < abs(ratio2 - 0.15)) binary1.clone() else binary2.clone()

        binary1.release()
        binary2.release()
        println("$result +aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaresult result result result")
        return result
    }
}
