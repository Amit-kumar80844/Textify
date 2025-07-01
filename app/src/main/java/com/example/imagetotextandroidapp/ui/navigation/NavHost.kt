package com.example.imagetotextandroidapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.imagetotextandroidapp.ui.screen.imageExtractor.ImageExtractionScreen
import com.example.imagetotextandroidapp.ui.screen.splash.SplashScreen

@Composable
fun Navigate(
    navHostController: NavHostController
) {
    /* for test start destination is not splash screen */
    NavHost(navController = navHostController, startDestination = NavGraph.ImageExtractor.route) {
        composable(route = NavGraph.Splash.route) {
            SplashScreen(navHostController)
        }
        composable(route = NavGraph.ImageExtractor.route) {
            ImageExtractionScreen(navHostController)
        }
    }
}