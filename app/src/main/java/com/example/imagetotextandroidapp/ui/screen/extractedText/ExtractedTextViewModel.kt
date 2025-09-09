package com.example.imagetotextandroidapp.ui.screen.extractedText

import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagetotextandroidapp.data.localDatabase.TextDao
import com.example.imagetotextandroidapp.data.localDatabase.TextEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExtractedTextViewModel @Inject constructor(
    private val textDao: TextDao
) : ViewModel() {

    private val _navigationEvent = Channel<Unit>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    /**
     * Saves the extracted text to the local database.
     *
     * @param text The text to be saved.
     */
    fun saveText(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                textDao.insertText(
                    TextEntity(extractedText = text)
                )
                _navigationEvent.send(Unit)
            } catch (e: Exception) {
                // Log any errors that occur during the database operation.
                e("Database Error", "Error saving text: ${e.message}")
            }
        }
    }
}
