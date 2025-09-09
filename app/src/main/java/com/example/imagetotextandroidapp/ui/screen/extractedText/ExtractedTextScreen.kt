package com.example.imagetotextandroidapp.ui.screen.extractedText

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
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

/**
 * Stateless composable that displays the UI based on the provided state.
 * This makes the UI easier to test and preview.
 */
@Composable
fun ExtractedTextContent(
    extractedText: String,
    onCopyClick: (String) -> Unit,
    onShareClick: (String) -> Unit,
    onDoneClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Extracted Text",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = extractedText,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Changed icon to ContentCopy for better UX.
            ActionButton(icon = Icons.Default.ContentCopy, label = "Copy") { onCopyClick(extractedText) }
            ActionButton(icon = Icons.Filled.Share, label = "Share") { onShareClick(extractedText) }
            ActionButton(icon = Icons.Filled.Done, label = "Done") { onDoneClick(extractedText) }
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExtractedTextPreview() {
    val dummyText =
        "This is a sample extracted text from an image. It can be quite long, so it should be displayed in a scrollable text area to ensure that all content is accessible to the user."

    ExtractedTextContent(
        extractedText = dummyText,
        onCopyClick = {},
        onShareClick = {},
        onDoneClick = {}
    )
}