package com.example.imagetotextandroidapp.data.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController

fun takeImage(
    navHostController: NavHostController,
    context: Context,
    controller: LifecycleCameraController,
    onImageCaptured: (Bitmap) -> Unit
) {
    Log.d("DEBUG", "888888888888888888888888888captureImage() called")
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val bitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )
                Log.d("CameraCapture", "Image captured successfully")
                image.close()
                onImageCaptured(bitmap)
            }
            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("CameraCapture", "Image capture failed: ${exception.message}", exception)
            }
        }
    )
}