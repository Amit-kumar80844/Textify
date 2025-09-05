package com.example.imagetotextandroidapp.data.image

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Extracts text from a bitmap image using ML Kit's Text Recognition.
 *
 * @param bitmap The input bitmap image.
 * @return Extracted text as a String.
 */
class TextExtractor @Inject constructor() {
    suspend operator fun invoke(bitmap: Bitmap): String {
        return extractTextFromBitmap(bitmap)
    }
    private suspend fun extractTextFromBitmap(bitmap: Bitmap): String {
        return withContext(Dispatchers.IO) {
            // Create InputImage from Bitmap
            val image = InputImage.fromBitmap(bitmap, 0)
            // Get recognizer instance
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            try {
                val result: Text = recognizer.process(image).await()
                result.text // Return full recognized text
            } catch (e: Exception) {
                e.printStackTrace()
                "Error: ${e.localizedMessage}"
            }
        }
    }
}
