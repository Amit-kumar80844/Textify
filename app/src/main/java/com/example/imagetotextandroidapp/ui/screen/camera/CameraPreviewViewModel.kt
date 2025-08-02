package com.example.imagetotextandroidapp.ui.screen.camera

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.imagetotextandroidapp.data.image.takeImage
import com.example.imagetotextandroidapp.ui.navigation.NavGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.util.Log

sealed class CropState {
    object Idle : CropState()
    object Cropping : CropState()
    object Success : CropState()
    data class Error(val message: String) : CropState()
}

@HiltViewModel
class CameraPreviewViewModel @Inject constructor() : ViewModel() {
    private val _capturedImage = MutableLiveData<Bitmap?>()
    val capturedImage: LiveData<Bitmap?> = _capturedImage

    var croppedImage by mutableStateOf<Bitmap?>(null)
        private set

    var cropState by mutableStateOf<CropState>(CropState.Idle)
        private set

    fun updateCroppedImage(bitmap: Bitmap) {
        croppedImage = bitmap
        cropState = CropState.Success
    }

    fun setCropError(error: String) {
        cropState = CropState.Error(error)
    }

    fun startCropping() {
        cropState = CropState.Cropping
    }

    fun resetCrop() {
        croppedImage = null
        cropState = CropState.Idle
    }

    // Camera related methods
    fun captureImage(
        context: Context,
        controller: LifecycleCameraController,
        navController: NavHostController
    ) {
        Log.d("CameraPreviewViewModel", "Capture Image Button clicked")
        takeImage(navController, context, controller) { bitmap ->
            Log.d("CameraPreviewViewModel", "onImageCaptured called with bitmap: ${bitmap.width}x${bitmap.height}")

            // Set the captured image immediately
            _capturedImage.value = bitmap

            Log.d("CameraPreviewViewModel", "Captured image set, navigating to CropScreen")

            // Navigate to crop screen
            navController.navigate(NavGraph.CropScreen.route)
        }
    }

    fun clearCapturedImage() {
        _capturedImage.value = null
        resetCrop() // Also reset crop state
    }

    fun getCapturedImage(): Bitmap? {
        return _capturedImage.value
    }

    fun isImageCaptured(): Boolean {
        return _capturedImage.value != null
    }

    // Update captured image with cropped version
    fun updateCapturedImage(bitmap: Bitmap) {
        _capturedImage.value = bitmap // Use .value instead of .postValue for immediate update
    }

    fun cancelCamera(navHostController: NavHostController) {
        // Clear the captured image before navigating back
        clearCapturedImage()
        navHostController.popBackStack()
    }
}