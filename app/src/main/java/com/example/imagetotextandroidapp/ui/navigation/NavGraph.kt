package com.example.imagetotextandroidapp.ui.navigation

sealed class NavGraph(
    val route: String
) {
    data object Splash : NavGraph("Splash")
    data object Home : NavGraph("Home")
}