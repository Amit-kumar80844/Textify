package com.example.imagetotextandroidapp.ui.screen.camera

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun CropScreenHelper(
    navHostController: NavHostController
) {
    val viewModel: CameraPreviewViewModel = hiltViewModel()
    val capturedImage = viewModel.getCapturedImage()

    if (capturedImage == null) {
        LaunchedEffect(Unit) {
            navHostController.popBackStack()
        }
        return
    }

    CropScreen(
        originalBitmap = capturedImage,
        viewModel = viewModel,
        onCropComplete = { croppedBitmap ->
            viewModel.updateCapturedImage(croppedBitmap)
            // Navigate to the next screen with the cropped image
        },
        onCancel = {
            navHostController.popBackStack()
        }
    )
}

@Composable
fun CropScreen(
    originalBitmap: Bitmap,
    viewModel: CameraPreviewViewModel = hiltViewModel(),
    onCropComplete: (Bitmap) -> Unit,
    onCancel: () -> Unit
) {
    val cropState = viewModel.cropState
    val croppedImage = viewModel.croppedImage

    when (val state = cropState) {
        is CropState.Idle -> {
            LaunchedEffect(Unit) {
                viewModel.startCropping()
            }
            StartCroppingUI(
                bitmap = originalBitmap,
                onCropped = { bitmap ->
                    viewModel.setCroppedImage(bitmap)
                },
                onError = { error ->
                    viewModel.setCropError(error)
                },
                onCancel = {
                    onCancel()
                }
            )
        }

        is CropState.Cropping -> {
            StartCroppingUI(
                bitmap = originalBitmap,
                onCropped = { bitmap ->
                    viewModel.setCroppedImage(bitmap)
                },
                onError = { error ->
                    viewModel.setCropError(error)
                },
                onCancel = {
                    onCancel()
                }
            )
        }

        is CropState.Success -> {
            croppedImage?.let { bitmap ->
                CroppedImagePreview(
                    croppedBitmap = bitmap,
                    onAccept = {
                        onCropComplete(bitmap)
                    },
                    onRetry = {
                        viewModel.resetCrop()
                    },
                    onCancel = onCancel
                )
            }
        }
        is CropState.Error -> {
            CropErrorScreen(
                error = state.message,
                onRetry = { viewModel.resetCrop() },
                onCancel = onCancel
            )
        }
    }
}

@Composable
fun CroppedImagePreview(
    croppedBitmap: Bitmap,
    onAccept: () -> Unit,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Image preview
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Image(
                bitmap = croppedBitmap.asImageBitmap(),
                contentDescription = "Cropped image preview",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Close, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onAccept,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Accept")
            }
        }
    }
}

@Composable
fun CropErrorScreen(
    error: String,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Crop Failed",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )

                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onCancel) {
                        Text("Cancel")
                    }

                    Button(onClick = onRetry) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}