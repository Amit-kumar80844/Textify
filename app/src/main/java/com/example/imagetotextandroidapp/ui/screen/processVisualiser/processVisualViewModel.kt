package com.example.imagetotextandroidapp.ui.screen.processVisualiser

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProcessVisualViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ProcessVisualState())
    val uiState: StateFlow<ProcessVisualState> = _uiState.asStateFlow()
    val progress = uiState.value.progress
    val isCancel = uiState.value.isCancel
    val isProcessing =  uiState.value.isProcessing
    val isError =  uiState.value.isError
    val errorMessage =  uiState.value.errorMessage
    val isSuccess =  uiState.value.isSuccess

    fun updateProgress(newProgress: Float) {
        _uiState.value = _uiState.value.copy(progress = newProgress)
    }
    fun cancelProcess() {
        _uiState.value = _uiState.value.copy(isCancel = true)
    }
    fun startProcessing() {
        _uiState.value = _uiState.value.copy(isProcessing = true, isError = false, isSuccess = false)
    }
    fun processError(message: String) {
        _uiState.value = _uiState.value.copy(isError = true, errorMessage = message, isProcessing = false)
    }
    fun processSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = true, isProcessing = false)
    }
    fun isSuccessful(): Boolean {
        return _uiState.value.isSuccess
    }
    fun resetState() {
        _uiState.value = ProcessVisualState.Initial
    }

}