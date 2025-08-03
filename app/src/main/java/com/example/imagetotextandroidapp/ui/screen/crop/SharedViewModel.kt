package com.example.imagetotextandroidapp.ui.screen.crop

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
    private val _capturedImage = MutableLiveData<Bitmap?>()
    val capturedImage: LiveData<Bitmap?> = _capturedImage

    fun setImage(bitmap: Bitmap) {
        _capturedImage.value = bitmap
    }

    fun clearImage() {
        _capturedImage.value = null
    }

    fun getCapturedImage(): Bitmap? {
        return _capturedImage.value
    }
}
