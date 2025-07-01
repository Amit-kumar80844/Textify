package com.example.imagetotextandroidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.imagetotextandroidapp.ui.navigation.Navigate
import com.example.imagetotextandroidapp.ui.theme.ImageTOTextAndroidAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageTOTextAndroidAppTheme {
                Navigate(rememberNavController())
            }
        }
    }
}