package com.example.imagetotextandroidapp.ui.screen.extractedText

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.imagetotextandroidapp.ui.screen.crop.SharedViewModel
import com.example.imagetotextandroidapp.ui.screen.imageExtractor.ActionButton

@Composable
fun ExtractedTextScreen(
    navHostController: NavHostController,
    sharedViewModel: SharedViewModel,
) {
    val  viewModel: ExtractedTextViewModel = hiltViewModel()
    ExtractedTextContent(viewModel,sharedViewModel)
}

@Composable
fun ExtractedTextContent(
    viewModel: ExtractedTextViewModel,
    sharedViewModel: SharedViewModel
) {
    val extractedText = sharedViewModel.textFromImage.observeAsState()
    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ){
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = extractedText.value.toString(),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionButton(icon = Icons.Filled.ThumbUp, label = "Copy") { /* after copy image shold bw changed Copy */ }
            ActionButton(icon = Icons.Filled.Share, label = "Share") { /* Share */ }
            ActionButton(icon = Icons.Filled.Refresh, label = "Try Again") { /* Try Again */ }
        }
    }
}
