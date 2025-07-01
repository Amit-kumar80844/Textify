package com.example.imagetotextandroidapp.ui.screen.imageExtractor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.imagetotextandroidapp.ui.theme.ImageTOTextAndroidAppTheme

@Composable
fun ImageExtractionScreen(navController: NavHostController) {
    /*hilt view model will here*/
    TextExtractorScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextExtractorScreen() {
    val extractedText = remember { mutableStateOf("Extracted text will appear here.") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sticky Header
        TopAppBar(
            title = {
                Text(
                    "Text Extractor",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            navigationIcon = {
                IconButton(onClick = { /* Handle back */ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface) // Use surface for a cohesive background
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select an image from your gallery or take a new photo to extract text.",
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Slightly less prominent than onSurface
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { /* Select image */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(0.8f)
                ) {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Image", color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp)
                }

                OutlinedButton(
                    onClick = { /* Take photo */ },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(0.8f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Take Photo", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Image Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(16f / 9f)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)), // Lighter background for placeholder
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Extract Text Button
            Button(
                onClick = { /* Extract text */ },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(0.8f)
            ) {
                Icon(Icons.Filled.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Extract Text", color = MaterialTheme.colorScheme.onPrimary, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Extracted Text Box
            Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                Text(
                    text = "Extracted Text:",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = extractedText.value,
                    onValueChange = { extractedText.value = it },
                    placeholder = { Text("Your extracted text will appear here...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionButton(icon = Icons.Filled.ThumbUp, label = "Copy") { /* after copy image shold bw changed Copy */ }
                ActionButton(icon = Icons.Filled.Share, label = "Share") { /* Share */ }
                ActionButton(icon = Icons.Filled.Refresh, label = "Try Again") { /* go back to select a new image as new app will opened */ }
            }
        }
    }
}

@Composable
fun ActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
@Preview
fun AppPreview() {
    ImageTOTextAndroidAppTheme {
        ImageExtractionScreen(navController = NavHostController(LocalContext.current))
    }
}

@Composable
@Preview
fun AppPreviewdark() {
    ImageTOTextAndroidAppTheme(
        darkTheme = true
    ) {
        ImageExtractionScreen(navController = NavHostController(LocalContext.current))
    }
}