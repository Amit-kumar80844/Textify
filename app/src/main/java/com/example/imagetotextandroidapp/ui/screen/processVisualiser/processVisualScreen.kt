package com.example.imagetotextandroidapp.ui.screen.processVisualiser

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
import androidx.navigation.compose.rememberNavController
import com.example.imagetotextandroidapp.R
import kotlinx.coroutines.delay

@Composable
fun ProcessForImage(navHostController: NavHostController,
                    processVisualViewModel: ProcessVisualViewModel = hiltViewModel()) {
    ProcessVisualScreen(processVisualViewModel)
}

@Preview
@Composable
fun ProcessForImagePreview() {
    val navHostController = rememberNavController()
    val processVisualViewModel = ProcessVisualViewModel()
    ProcessForImage(navHostController = navHostController, processVisualViewModel = processVisualViewModel)
}


/**
 * [onClick] should be recheck again
 * */

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
    val processVisualViewModel = ProcessVisualViewModel()
    ProcessVisualScreen(processVisualViewModel = processVisualViewModel)
}


@Composable
fun LinearDeterminateIndicator(processVisualViewModel: ProcessVisualViewModel) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
          LinearProgressIndicator(
                progress = { processVisualViewModel.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                gapSize = 0.dp
          )
    }
}

@Preview
@Composable
fun LinearDeterminateIndicatorPreview() {
    val processVisualViewModel = ProcessVisualViewModel()
    LinearDeterminateIndicator(processVisualViewModel = processVisualViewModel)
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

@Preview
@Composable
fun CustomTextPreview() {
    CustomText()
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

@Preview
@Composable
fun LoadingDotsAnimationPreview() {
    LoadingDotsAnimation()
}
