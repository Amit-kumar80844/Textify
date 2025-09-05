package com.example.imagetotextandroidapp.ui.screen.processVisualiser

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagetotextandroidapp.data.image.ImagePreProcessor
import com.example.imagetotextandroidapp.data.image.TextExtractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class ProcessVisualState {
    data class Progress(val value: Float) : ProcessVisualState()
    object IsCancel : ProcessVisualState()
    data class IsError(val value: String) : ProcessVisualState()
    object IsSuccess : ProcessVisualState()
}

@HiltViewModel
class ProcessVisualViewModel @Inject constructor(
    private val imagePreProcessor: ImagePreProcessor,
    private val textExtractor: TextExtractor
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProcessVisualState>(ProcessVisualState.Progress(0f))
    val uiState: StateFlow<ProcessVisualState> = _uiState.asStateFlow()

    private val _extractedText = MutableStateFlow<String>("")
    val extractedText: StateFlow<String> = _extractedText.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private var processingJob: Job? = null

    fun updateProgress(value: Float) {
        _progress.value = value
        _uiState.value = ProcessVisualState.Progress(value)
    }

    fun processImageToText(image: Bitmap) {
        // Cancel any existing processing
        processingJob?.cancel()

        processingJob = viewModelScope.launch {
            try {
                updateProgress(0f)

                // Step 1: Preprocess image (run in background)
                val preprocessedImage = withContext(Dispatchers.Default) {
                    imagePreProcessor.preprocessImageForOCR(image)
                }
                updateProgress(0.5f)

                // Step 2: Extract text (also run in background)
                val extractedTextResult = withContext(Dispatchers.Default) {
                    textExtractor(preprocessedImage)
                }

                // Step 3: Update LiveData on Main thread
                _extractedText.value = extractedTextResult
                updateProgress(1.0f)

                // Small delay for UX
                delay(500)
                processSuccess()

            } catch (e: Exception) {
                processError(e.toString())
            }
        }
    }


    fun cancelProcess() {
        processingJob?.cancel()
        _uiState.value = ProcessVisualState.IsCancel
        resetProgress()
    }

    fun processError(errorMessage: String = "An error occurred during processing.") {
        _uiState.value = ProcessVisualState.IsError(errorMessage)
    }

    fun processSuccess() {
        _uiState.value = ProcessVisualState.IsSuccess
    }

    fun resetState() {
        processingJob?.cancel()
        _uiState.value = ProcessVisualState.Progress(0f)
        _extractedText.value = ""
        resetProgress()
    }

    private fun resetProgress() {
        _progress.value = 0f
    }

    override fun onCleared() {
        super.onCleared()
        processingJob?.cancel()
    }
}