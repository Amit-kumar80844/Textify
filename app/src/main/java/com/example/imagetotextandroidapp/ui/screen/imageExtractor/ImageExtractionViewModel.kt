package com.example.imagetotextandroidapp.ui.screen.imageExtractor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.serialization.Serializable
import java.io.InputStream
import javax.inject.Inject

sealed class ImageState(){
    object IsIdle: ImageState()
    object IsLoading: ImageState()
    object IsImageSelecting: ImageState()
    object IsImageSelected: ImageState()
    object IsPreviousText: ImageState()
}

/*
* state management is not good make  in imageextracton screen make it better
* also have state something in prev screen
* */

@HiltViewModel
class ImageExtractionViewModel @Inject constructor(
    private val permission: Permission
) : ViewModel() {
    private val _imageState = MutableLiveData<ImageState>(ImageState.IsIdle)
    val imageState: LiveData<ImageState> get() = _imageState

    fun setImageState(state: ImageState) {
        _imageState.value = state
    }
    var selectImageUri: Uri? by mutableStateOf(null)

    fun manageCameraPermission(){
        if(!permission.hasCameraPermission()){
            permission.requestCameraPermission()
        }
    }
    fun hasCameraPermission(): Boolean {
        return permission.hasCameraPermission()
    }
    /**
     * Convert URI to Bitmap
     */
     fun uriToBitmap(uri: Uri, context: Context): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: Exception) {
            throw Exception("Error converting URI to Bitmap: ${e.message}")
        }
    }
}