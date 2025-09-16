package com.example.imagetotextandroidapp.ui.screen.previousText

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.imagetotextandroidapp.ui.screen.lodingScreen.LoadingScreen
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.imagetotextandroidapp.ui.screen.common.ExtractedTextContent
import com.example.imagetotextandroidapp.ui.screen.imageExtractor.TextExtractorScreen
import com.example.imagetotextandroidapp.ui.theme.ImageTOTextAndroidAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PreviousTextScreen(navHostController: NavHostController) {
    val viewModel : PreviousTextViewModel = hiltViewModel()
    val previousTextState by viewModel.previousTextState.observeAsState(PreviousTextState.Loading)
    val previousTexts by viewModel.previousTexts.observeAsState(emptyList())
    val text by viewModel.text.observeAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

   Column(
         modifier = Modifier
             .fillMaxSize()
             .background(MaterialTheme.colorScheme.background)
   ) {
       when(val state = previousTextState){
           PreviousTextState.Loading -> {
               LaunchedEffect(Unit) {
                   viewModel.fetchPreviousTexts()
               }
           }

           is PreviousTextState.Success -> {
               if(previousTexts.isEmpty()) {
                   LaunchedEffect(Unit) {
                     viewModel.noTextState()
                   }
               }else
                   PreviousTextList(
                       texts = previousTexts,
                       onTextClick = { clickedText ->
                           viewModel.getFullText((clickedText.id))
                       },
                       onDeleteClick = { textToDelete ->
                           coroutineScope.launch {
                               viewModel.deleteText(textToDelete.id)
                           }
                       }
                   )
           }
           is PreviousTextState.Error -> {
               ErrorScreen(message = state.message)
                LaunchedEffect(Unit) {
                     delay(1000)
                    navHostController.popBackStack()
                }
           }
           is PreviousTextState.Fulltext -> {
               if(previousTexts.isEmpty()) {
                   LaunchedEffect(Unit) {
                       viewModel.noTextState()
                   }
               }
               ExtractedTextContent(
                   extractedText = text?.text ?: "",
                   onCopyClick = { copiedText ->
                       clipboardManager.setText(AnnotatedString(copiedText))
                       Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
                   },
                   onShareClick = { sharedText ->
                       val sendIntent = Intent().apply {
                           action = Intent.ACTION_SEND
                           putExtra(Intent.EXTRA_TEXT, sharedText)
                           type = "text/plain"
                       }
                       val shareIntent = Intent.createChooser(sendIntent, null)
                       context.startActivity(shareIntent)
                   },
                   onDoneClick = {
                       coroutineScope.launch {
                           viewModel.loadingState()
                           delay(2000)
                           viewModel.successState("Back to list")
                       }
                   },
                   modifier = Modifier.weight(1f)
               )
           }
           PreviousTextState.NavigateBack -> {
               LaunchedEffect(Unit) {
                   navHostController.popBackStack()
               }
           }
       }
   }
}

@Composable
@Preview
fun AppPreview() {
    ImageTOTextAndroidAppTheme {
        TextExtractorScreen(NavHostController(LocalContext.current), hiltViewModel())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviousTextList(
    texts: List<TextData>,
    onTextClick: (TextData) -> Unit,
    onDeleteClick: (TextData) -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.onPrimary),
        topBar = {
            TopAppBar(
                title = { Text("Previous Texts", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(texts) { textData ->
                TextCardItem(
                    textData = textData,
                    onClick = { onTextClick(textData) },
                    onDeleteClick = { onDeleteClick(textData) } // Pass delete handler
                )
            }
        }
    }
}

@Composable
fun TextCardItem(
    textData: TextData,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(150.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = textData.text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 4, // Limits text to 4 lines
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = textData.date,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Text",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//@Preview
//fun PreviousTextListPreview() {
//    var st = "heeloo".repeat(12)
//    val sampleTexts = listOf(
//        TextData(id = 1, text = st, date = "2023-01-01"),
//        TextData(id = 2, text = "Another Text", date = "2023-01-02"),
//        TextData(id = 3, text = "Compose Preview", date = "2023-01-03")
//    )
//    PreviousTextList(texts = sampleTexts, onTextClick = {})
//}
//

@Composable
fun ErrorScreen(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme .colorScheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.Delete, // Replace with an appropriate error icon
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = message, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.error)
        }
    }
}
//@Composable
//@Preview
//fun previewErrorScreen() {
//    ErrorScreen(message = "An error occurred while fetching texts.")
//}