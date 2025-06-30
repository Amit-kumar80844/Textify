package com.example.imagetotextandroidapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.imagetotextandroidapp.ui.screen.Home.HomeScreen
import com.example.imagetotextandroidapp.ui.screen.splash.SplashScreen

@Composable
fun Navigate(
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = NavGraph.Splash.route) {
        composable(route = NavGraph.Splash.route) {
            SplashScreen(navHostController)
        }
        composable(route = NavGraph.Home.route){
            HomeScreen(navHostController)
        }
    }
}