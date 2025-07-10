package com.example.imagetotextandroidapp.ui.screen.imageExtractor

data class ImageExtractionState(
    val isLoading: Boolean = false,
    val extractedText: String = "",
    val errorMessage: String? = null,
    val isImageSelected: Boolean = false,
    val isImageProcessing: Boolean = false,
    val isImageExtractionSuccessful: Boolean = false,
    val isImageExtractionFailed: Boolean = false,
    val isImageExtractionInProgress: Boolean = false,
    val selectImageButtonEnabled: Boolean = true,
    val extractTextButtonEnabled: Boolean = false,

)