package com.example.imagetotextandroidapp.ui.screen.crop

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
//    captured image from camera
    private val _capturedImage = MutableLiveData<Bitmap?>()
    val capturedImage: LiveData<Bitmap?> = _capturedImage

    private val _textFromImage = MutableLiveData<String>()
    var textFromImage: LiveData<String> = _textFromImage

    fun setImage(bitmap: Bitmap) {
        _capturedImage.value = bitmap
    }
    fun updateTextFromImage(text: String) {
        _textFromImage.value = text
    }

    fun clearImage() {
        _capturedImage.value = null
    }

    fun getCapturedImage(): Bitmap? {
        return _capturedImage.value
    }
    fun clearAll(){
        _capturedImage.value = null
        _textFromImage.value = ""
    }
}