package com.example.imagetotextandroidapp.ui.screen.extractedText

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.imagetotextandroidapp.ui.screen.common.ExtractedTextContent
import com.example.imagetotextandroidapp.ui.screen.crop.SharedViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Stateful composable that connects the UI to the ViewModel.
 * This composable is responsible for handling UI logic like navigation and intents.
 */
@Composable
fun ExtractedTextScreen(
    navHostController: NavHostController,
    sharedViewModel: SharedViewModel,
    viewModel: ExtractedTextViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val extractedText by sharedViewModel.textFromImage.observeAsState("")
    LaunchedEffect(viewModel) {
        viewModel.navigationEvent.collectLatest {
            sharedViewModel.clearAll()
            Toast.makeText(context, "Text saved successfully!", Toast.LENGTH_SHORT).show()
            navHostController.navigate("ImageExtractor") {
                popUpTo("ExtractedText") { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    ExtractedTextContent(
        extractedText = extractedText,
        onCopyClick = { text ->
            clipboardManager.setText(AnnotatedString(text))
            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
        },
        onShareClick = { text ->
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        },
        onDoneClick = { text ->
            viewModel.saveText(text)
        }
    )
}
