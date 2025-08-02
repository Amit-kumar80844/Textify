package com.example.imagetotextandroidapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.imagetotextandroidapp.ui.screen.camera.CameraScreen
import com.example.imagetotextandroidapp.ui.screen.camera.CropScreenHelper
import com.example.imagetotextandroidapp.ui.screen.extractedText.ExtractedTextScreen
import com.example.imagetotextandroidapp.ui.screen.imageExtractor.ImageExtractionScreen
import com.example.imagetotextandroidapp.ui.screen.imagePreview.ImagePreviewScreen
import com.example.imagetotextandroidapp.ui.screen.processVisualiser.ProcessForImage
import com.example.imagetotextandroidapp.ui.screen.splash.SplashScreen

@Composable
fun Navigate(
    navHostController: NavHostController
) {
    /* for test start destination is not splash screen */
    NavHost(navController = navHostController, startDestination =NavGraph.ImageExtractor.route) {
        composable(route = NavGraph.Splash.route) {
            SplashScreen(navHostController)
        }
        composable(route = NavGraph.ImageExtractor.route) {
            ImageExtractionScreen(navHostController)
        }
        composable (route = NavGraph.ProcessVisualiser.route) {
            ProcessForImage(navHostController)
        }
        composable(route = NavGraph.ExtractedText.route) {
            ExtractedTextScreen(navHostController)
        }
        composable(route = NavGraph.CameraPreview.route){
            CameraScreen(navHostController)
        }
        composable(route = NavGraph.CropScreen.route) {
             CropScreenHelper(navHostController)
        }
        composable(route = NavGraph.ImagePreview.route){
            ImagePreviewScreen(navHostController)
        }
    }
}