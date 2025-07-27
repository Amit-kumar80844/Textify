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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

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

    fun setCroppedImage(bitmap: Bitmap) {
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
    fun captureImage(context: Context, controller: LifecycleCameraController,navController: NavHostController) {
        takeImage(context, controller) { bitmap ->
            _capturedImage.postValue(bitmap)
        }
        navController.navigate("CropScreen")
    }

    fun clearCapturedImage() {
        _capturedImage.value = null
    }

    fun getCapturedImage(): Bitmap? {
        return _capturedImage.value
    }

    fun isImageCaptured(): Boolean {
        return _capturedImage.value != null
    }

    // Update captured image with cropped version
    fun updateCapturedImage(bitmap: Bitmap) {
        _capturedImage.postValue(bitmap)
    }
    fun cancelCamera(navHostController: NavHostController){
        clearCapturedImage()
        navHostController.popBackStack()
    }
}