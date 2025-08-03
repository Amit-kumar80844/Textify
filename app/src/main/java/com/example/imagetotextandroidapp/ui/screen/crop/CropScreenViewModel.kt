package com.example.imagetotextandroidapp.ui.screen.crop

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class CropState {
    object Idle : CropState()
    object Cropping : CropState()
    object Success : CropState()
    data class Error(val message: String) : CropState()
}

@HiltViewModel
class CropScreenViewModel @Inject constructor() : ViewModel() {

    var cropState by mutableStateOf<CropState>(CropState.Idle)
        private set

    var croppedImage by mutableStateOf<Bitmap?>(null)
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

    fun setCapturedImage(bitmap: Bitmap, onImageCaptured: (Bitmap) -> Unit) {
        croppedImage = bitmap
        onImageCaptured(bitmap)
        cropState = CropState.Success
    }
}