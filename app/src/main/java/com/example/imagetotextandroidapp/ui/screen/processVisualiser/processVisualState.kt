package com.example.imagetotextandroidapp.ui.screen.processVisualiser

data class ProcessVisualState(
    val progress: Float = 0f,
    val isCancel: Boolean = false,
    val isProcessing: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val isSuccess: Boolean = false
) {
    companion object {
        val Initial = ProcessVisualState()
    }
}