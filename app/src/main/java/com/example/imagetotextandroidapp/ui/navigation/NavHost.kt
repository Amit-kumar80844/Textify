package com.example.imagetotextandroidapp.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.imagetotextandroidapp.ui.screen.camera.CameraScreen
import com.example.imagetotextandroidapp.ui.screen.crop.CropScreenHelper
import com.example.imagetotextandroidapp.ui.screen.crop.SharedViewModel
import com.example.imagetotextandroidapp.ui.screen.extractedText.ExtractedTextScreen
import com.example.imagetotextandroidapp.ui.screen.imageExtractor.ImageExtractionScreen
import com.example.imagetotextandroidapp.ui.screen.imagePreview.ImagePreviewScreen
import com.example.imagetotextandroidapp.ui.screen.processVisualiser.ProcessForImage
import com.example.imagetotextandroidapp.ui.screen.splash.SplashScreen

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun Navigate(
    navHostController: NavHostController
) {
    val sharedImageViewModel: SharedViewModel = hiltViewModel()

    NavHost(navController = navHostController, startDestination = NavGraph.ImageExtractor.route) {
        composable(route = NavGraph.Splash.route) {
            SplashScreen(navHostController)
        }
        composable(route = NavGraph.ImageExtractor.route) {
            ImageExtractionScreen(navHostController,sharedImageViewModel)
        }
        composable(route = NavGraph.ProcessVisualiser.route) {
            ProcessForImage(navHostController, sharedImageViewModel)
        }
        composable(route = NavGraph.ExtractedText.route) {
            ExtractedTextScreen(navHostController,sharedImageViewModel)
        }
        // shared screens here
        composable(route = NavGraph.CameraPreview.route) {
            CameraScreen(navHostController, sharedImageViewModel)
        }
        composable(route = NavGraph.CropScreen.route) {
            CropScreenHelper(navHostController, sharedImageViewModel)
        }
        composable(route = NavGraph.ImagePreview.route) {
            ImagePreviewScreen(navHostController, sharedImageViewModel)
        }
    }
}
