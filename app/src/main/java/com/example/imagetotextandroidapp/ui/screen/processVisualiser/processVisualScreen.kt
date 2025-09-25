package com.example.imagetotextandroidapp.ui.screen.processVisualiser

import android.util.Log
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.imagetotextandroidapp.R
import com.example.imagetotextandroidapp.ui.navigation.NavGraph
import com.example.imagetotextandroidapp.ui.screen.crop.SharedViewModel
import com.example.imagetotextandroidapp.ui.screen.lodingScreen.LoadingScreen
import kotlinx.coroutines.delay

@Composable
fun ProcessForImage(
    navHostController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    val processVisualViewModel: ProcessVisualViewModel = hiltViewModel()
    val uiState by processVisualViewModel.uiState.collectAsState()
    val extractedText by processVisualViewModel.extractedText.collectAsState()

    when (val state = uiState){
        is ProcessVisualState.Progress -> {
            val progressValue = state.value

            val capturedImage = sharedViewModel.capturedImage.value
            LaunchedEffect(capturedImage) {
                capturedImage?.let {
                    processVisualViewModel.processImageToText(it)
                }
            }

            LaunchedEffect(progressValue) {
                if (progressValue >= 1.0f) {
                    processVisualViewModel.processSuccess()
                }
            }

            ProcessVisualScreen(processVisualViewModel)
        }

        is ProcessVisualState.IsCancel -> {
            var loading by remember { mutableStateOf(true) }

            if (loading) {
                LoadingScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                )
            }

            LaunchedEffect(Unit) {
                sharedViewModel.clearAll()
                delay(1000)
                navHostController.popBackStack(
                    route = NavGraph.ImageExtractor.route,
                    inclusive = false
                )
                loading = false
            }
        }
        is ProcessVisualState.IsError -> {
            ErrorScreen(state.message)
            sharedViewModel.clearAll()
            LaunchedEffect(Unit) {
                delay(2000)
                navHostController.popBackStack()
            }
        }

        is ProcessVisualState.IsSuccess -> {
            LaunchedEffect(extractedText) {
                sharedViewModel.updateTextFromImage(extractedText)
                Log.d("SuccessScreen", "Extracted Text: $extractedText")

                navHostController.navigate(NavGraph.ExtractedText.route) {
                    popUpTo("ProcessVisualiser") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }
}

@Composable
fun ProcessVisualScreen(processVisualViewModel: ProcessVisualViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
            LinearDeterminateIndicator(processVisualViewModel)
            CustomText()
            Spacer(modifier = Modifier.padding(40.dp))
            Button(
                onClick = {processVisualViewModel.cancelProcess()},/* here we are using to cancel process */
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(0.9f)
            ) {
                Icon(Icons.Filled.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cancel", color = MaterialTheme.colorScheme.onPrimary, fontSize = 16.sp)
            }
        }
        Spacer(modifier = Modifier.padding(2.dp))
    }
}

@Preview
@Composable
fun ProcessVisualScreenPreview() {
    val processVisualViewModel: ProcessVisualViewModel = hiltViewModel()
    ProcessVisualScreen(processVisualViewModel = processVisualViewModel)
}

@Composable
fun LinearDeterminateIndicator(
    processVisualViewModel: ProcessVisualViewModel
) {
    // Collect progress from ViewModel
    val uiState by processVisualViewModel.uiState.collectAsState()
    val progress = when (val state = uiState) {
        is ProcessVisualState.Progress -> state.value
        else -> 0f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        ),
        label = "progressAnimation"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            gapSize = 0.dp
        )
    }
}


@Composable
fun CustomText() {
        Row {
            Text(
                text = "Extracting Text From Image",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                fontSize = 16.sp,
            )
            LoadingDotsAnimation()
    }
}

@Composable
fun LoadingDotsAnimation() {
    var dotText by remember { mutableStateOf(".") }
    LaunchedEffect(Unit) {
        val dotStates = listOf(".  ", ".. ", "...")
        while (true) {
            dotText = dotStates[0]
            delay(500)
            dotText = dotStates[1]
            delay(500)
            dotText = dotStates[2]
            delay(500)
        }
    }
    Text(
        text = dotText,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun ErrorScreen(errorText: String = "An error occurred during processing.") {
    Log.e("ErrorScreen", "Error: $errorText")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.outline_error_24),
            contentDescription = "Error",
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = errorText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp
        )
    }
}