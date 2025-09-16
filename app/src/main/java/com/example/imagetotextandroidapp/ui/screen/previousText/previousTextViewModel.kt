package com.example.imagetotextandroidapp.ui.screen.previousText

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.imagetotextandroidapp.data.localDatabase.TextDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class TextData(
    val id: Int,
    val text: String,
    val date: String
)

sealed class PreviousTextState {
    object Loading : PreviousTextState()
    data class Success(val success: String) : PreviousTextState()
    data class Error(val message: String) : PreviousTextState()
    data class Fulltext(val text: TextData) : PreviousTextState()
    object NavigateBack : PreviousTextState()
}

@HiltViewModel
class PreviousTextViewModel @Inject constructor(
    private val textDao: TextDao
) : ViewModel() {
    private val _previousTextState = MutableLiveData<PreviousTextState>(PreviousTextState.Loading)
    val previousTextState: LiveData<PreviousTextState> get() = _previousTextState

    private val _previousTexts = MutableLiveData<List<TextData>>(emptyList())
    val previousTexts: LiveData<List<TextData>> get() = _previousTexts

    private val _text = MutableLiveData<TextData>()
    val text: LiveData<TextData> get() = _text

    fun successState(message: String) {
        _previousTextState.value = PreviousTextState.Success(message)
    }

    suspend fun noTextState() {
        _previousTextState.value = PreviousTextState.Loading
        kotlinx.coroutines.delay(1000)
        _previousTextState.value = PreviousTextState.Error("No previous texts found")
    }

    suspend fun fetchPreviousTexts() {
        try {
            val texts = textDao.getAllText().map {
                TextData(
                    id = it.id,
                    text = it.extractedText,
                    date = it.timestamp.toString()
                )
            }
            _previousTexts.value = texts
            if(texts.isEmpty()) {
                _previousTextState.value = PreviousTextState.Error("No previous texts found")
                return
            }
            _previousTextState.value = PreviousTextState.Success("Fetched ${texts.size} texts")
        } catch (e: Exception) {
            _previousTextState.value = PreviousTextState.Error("Error fetching texts: ${e.message}")
        }
    }

    suspend fun deleteText(id: Int) {
        try {
            textDao.deleteTextById(id)
        } catch (e: Exception) {
            _previousTextState.value = PreviousTextState.Error("Error deleting text: ${e.message}")
        }
        fetchPreviousTexts()
    }

     fun getFullText(id: Int) {
         val textItem = previousTexts.value?.find { it.id == id }
         textItem?.let {
             _text.value = it
             _previousTextState.value = PreviousTextState.Fulltext(it)
         } ?: run {
             _previousTextState.value = PreviousTextState.Error("Text not found")
         }
     }

    fun loadingState() {
        _previousTextState.value = PreviousTextState.Loading
    }
}