package com.example.imagetotextandroidapp.ui.screen.imageExtractor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Permission @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun requestCameraPermission(): Array<String> {
        return CAMERA_PERMISSION.filter {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
    }

    fun hasCameraPermission(): Boolean {
        return CAMERA_PERMISSION.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERA_PERMISSION = arrayOf(
            Manifest.permission.CAMERA
        )
    }
}