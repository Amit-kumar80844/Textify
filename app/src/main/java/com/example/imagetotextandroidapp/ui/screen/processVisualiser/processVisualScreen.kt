package com.example.imagetotextandroidapp.ui.screen.processVisualiser
/*

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.imagetotextandroidapp.ui.theme.ImageTOTextAndroidAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.imagetotextandroidapp.R

@Composable
fun ProcessForImage() {
    ProcessVisualScreen()
}

@Preview(showSystemUi = false, showBackground = false)
@Composable
fun ProcessVisualScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Extracting Text",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(R.drawable.outline_convert_to_text_24),
                contentDescription = "Process Image",
                modifier = Modifier.size(150.dp)
            )
            LinearDeterminateIndicator()
            customText()
            Spacer(modifier = Modifier.padding(40.dp))
        }
    }
}

@Composable
fun LinearDeterminateIndicator() {
    var currentProgress by remember { mutableFloatStateOf(0.2f) }
    val loading = remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp)
    ) {
            LinearProgressIndicator(
                progress = { currentProgress },
                modifier = Modifier.fillMaxWidth()
                    .height(16.dp),
                color = Color(color = 0xFF0E462B),
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                gapSize = 0.dp
            )
    }
}

@Composable
fun customText() {
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(true) }
    if (loading) {
        scope.launch {
            delay(3000)
            loading = false
        }
    }
    if (loading) {
        Row {
            Text(
                text = "Extracting Text From Image",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
               Text(text = "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground)
        }
    } else {
        Text(
            text = "Text Extraction Completed",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}*/
