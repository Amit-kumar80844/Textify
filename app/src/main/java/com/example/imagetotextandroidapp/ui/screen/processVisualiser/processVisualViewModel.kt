package com.example.imagetotextandroidapp.ui.screen.processVisualiser

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagetotextandroidapp.data.image.ImagePreProcessor
import com.example.imagetotextandroidapp.data.image.TextExtractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed class ProcessVisualState {
    data class Progress(val value: Float) : ProcessVisualState()
    object IsCancel : ProcessVisualState()
    data class IsError(val message: String) : ProcessVisualState()
    object IsSuccess : ProcessVisualState()
}

@HiltViewModel
class ProcessVisualViewModel @Inject constructor(
    private val imagePreProcessor: ImagePreProcessor,
    private val textExtractor: TextExtractor
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProcessVisualState>(ProcessVisualState.Progress(0f))
    val uiState: StateFlow<ProcessVisualState> = _uiState.asStateFlow()

    private val _extractedText = MutableStateFlow("")
    val extractedText: StateFlow<String> = _extractedText.asStateFlow()

    private var processingJob: Job? = null

    private fun updateProgress(value: Float) {
        _uiState.value = ProcessVisualState.Progress(value)
    }

    fun processImageToText(image: Bitmap) {
        processingJob?.cancel()

        processingJob = viewModelScope.launch {
            try {
                updateProgress(0f)
                val preprocessedImage = withContext(Dispatchers.Default) {
                    imagePreProcessor.preprocessImageForOCR(image)
                }

                // Step 2: Extract text
                updateProgress(0.5f)
                val extractedTextResult = withContext(Dispatchers.Default) {
                    textExtractor(preprocessedImage)
                }
                // Step 3: Update result
                _extractedText.value = extractedTextResult
                updateProgress(1f)

                delay(1000)
                processSuccess()

            } catch (_: CancellationException) {
                _uiState.value = ProcessVisualState.IsCancel
            } catch (e: Exception) {
                processError(e.message ?: "Unexpected error")
            }
        }
    }

    fun cancelProcess() {
        processingJob?.cancel()
        _uiState.value = ProcessVisualState.IsCancel
        resetProgress()
    }

    private fun resetProgress() {
        _uiState.value = ProcessVisualState.Progress(0f)
    }

    fun processError(errorMessage: String) {
        _uiState.value = ProcessVisualState.IsError(errorMessage)
    }

    fun processSuccess() {
        _uiState.value = ProcessVisualState.Progress(1f) // force progress 100%
        viewModelScope.launch {
            delay(300)
            _uiState.value = ProcessVisualState.IsSuccess
        }
    }


    override fun onCleared() {
        super.onCleared()
        processingJob?.cancel()
    }
}
