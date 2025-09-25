package com.example.imagetotextandroidapp.ui.screen.crop

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class CropState {
    object Cropping : CropState()
    object Success : CropState()
    data class Error(val message: String) : CropState()
}

@HiltViewModel
class CropScreenViewModel @Inject constructor() : ViewModel() {

    var cropState by mutableStateOf<CropState>(CropState.Cropping)
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

    fun resetCrop() {
        croppedImage = null
        cropState = CropState.Cropping
    }

    fun setCapturedImage(bitmap: Bitmap, onImageCaptured: (Bitmap) -> Unit) {
        croppedImage = bitmap
        onImageCaptured(bitmap)
        cropState = CropState.Success
    }
    fun navigateToProcessScreen(navHostController: NavHostController){
        cropState = CropState.Cropping
        navHostController.navigate("ProcessVisualiser")
    }
}