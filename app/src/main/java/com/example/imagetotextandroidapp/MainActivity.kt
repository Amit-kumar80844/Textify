package com.example.imagetotextandroidapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.imagetotextandroidapp.ui.navigation.Navigate
import com.example.imagetotextandroidapp.ui.theme.ImageTOTextAndroidAppTheme
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val testDeviceIds = listOf(
            "229BD1B455F5B18D4E530B48E779EA30",
            AdRequest.DEVICE_ID_EMULATOR
        )
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(testDeviceIds)
            .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE)
            .build()
        MobileAds.setRequestConfiguration(configuration)

        // Now initialize AdMob
        MobileAds.initialize(this) {
            Log.d("AdMob", "AdMob initialized successfully")
        }
        setContent {
            ImageTOTextAndroidAppTheme {
                Navigate(rememberNavController())
            }
        }
    }
}