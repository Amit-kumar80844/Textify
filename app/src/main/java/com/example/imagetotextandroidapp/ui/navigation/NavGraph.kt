package com.example.imagetotextandroidapp.ui.navigation

sealed class NavGraph(
    val route: String
) {
    data object Splash : NavGraph("Splash")
    data object ImageExtractor : NavGraph("ImageExtractor")
    data object ProcessVisualiser : NavGraph("ProcessVisualiser")
    data object ExtractedText : NavGraph("ExtractedText")
    data object CameraPreview : NavGraph("CameraPreview")
    data object CropScreen : NavGraph("CropScreen")
    data object ImagePreview : NavGraph("ImagePreview")
}