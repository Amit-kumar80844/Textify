package com.example.imagetotextandroidapp.ui.screen.imageExtractor

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/* handle permission before it move to camera */
@HiltViewModel
class ImageExtractionViewModel @Inject constructor(
    private val permission: Permission
) : ViewModel() {
    fun requestCameraPermission(): Array<String> {
        return permission.requestCameraPermission()
    }

    fun requestStoragePermission(): Array<String> {
        return permission.requestStoragePermission()
    }

    fun hasCameraPermission(): Boolean {
        return permission.hasCameraPermission()
    }

    fun hasStoragePermission(): Boolean {
        return permission.hasStoragePermission()
    }
}