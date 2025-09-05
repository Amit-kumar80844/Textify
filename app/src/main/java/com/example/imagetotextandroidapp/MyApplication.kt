package com.example.imagetotextandroidapp

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import org.opencv.android.OpenCVLoader

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            System.loadLibrary("opencv_java4") // load native OpenCV
            Log.d("OpenCV", "Native OpenCV library loaded")
        } catch (e: UnsatisfiedLinkError) {
            Log.e("OpenCV", "Failed to load native OpenCV lib", e)
        }

        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "OpenCV initDebug() failed")
        } else {
            Log.d("OpenCV", "OpenCV initialized successfully")
        }
    }
}