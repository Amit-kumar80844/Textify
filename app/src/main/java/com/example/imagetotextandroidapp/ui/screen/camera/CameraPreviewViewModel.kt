package com.example.imagetotextandroidapp.ui.screen.camera

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.view.LifecycleCameraController
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.imagetotextandroidapp.data.image.takeImage
import com.example.imagetotextandroidapp.ui.navigation.NavGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class CameraPreviewViewModel @Inject constructor() : ViewModel() {

    fun captureImage(
        context: Context,
        controller: LifecycleCameraController,
        navController: NavHostController,
        onImageCaptured: (Bitmap) -> Unit
    ) {
        Log.d("CameraPreviewViewModel", "Capture Image Button clicked")
        takeImage(navController, context, controller) { bitmap ->
            Log.d(
                "CameraPreviewViewModel",
                "onImageCaptured called with bitmap: ${bitmap.width}x${bitmap.height}"
            )
            onImageCaptured(bitmap)
            Log.d("CameraPreviewViewModel", "Captured image set, navigating to CropScreen")
            navController.navigate(NavGraph.CropScreen.route)
        }
    }

    fun cancelCamera(navHostController: NavHostController) {
        navHostController.popBackStack()
    }
}