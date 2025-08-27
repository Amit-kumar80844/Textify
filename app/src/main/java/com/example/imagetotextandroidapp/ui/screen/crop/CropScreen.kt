package com.example.imagetotextandroidapp.ui.screen.crop

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.imagetotextandroidapp.R
import kotlinx.coroutines.delay
import androidx.core.graphics.createBitmap

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun CropScreenHelper(
    navHostController: NavHostController,
    sharedViewModel: SharedViewModel
){
    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        CropScreenMain(
            navHostController = navHostController,
            sharedViewModel = sharedViewModel
        )
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun CropScreenMain(
    navHostController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    val viewModel: CropScreenViewModel = hiltViewModel()
    val capturedImage by sharedViewModel.capturedImage.observeAsState()

    LaunchedEffect(capturedImage) {
        Log.d(
            "CropScreen",
            "CapturedImage state: ${capturedImage?.let { "${it.width}x${it.height}" } ?: "null"}"
        )
    }

    when {
        capturedImage == null -> {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Loading image...")
                    LaunchedEffect(Unit) {
                        delay(3000)
                        Log.d("CropScreen", "Timeout - no image found, navigating back")
                        navHostController.popBackStack()
                    }
                }
            }
        }

        else -> {
            CropScreen(
                originalBitmap = capturedImage!!,
                viewModel = viewModel,
                onCropComplete = { croppedBitmap ->
                    viewModel.setCapturedImage(croppedBitmap) { bitmap ->
                        sharedViewModel.setImage(bitmap)
                        Log.d("CropScreen", "Cropped image set, navigating to ImagePreview")
                    }
                },
                onCancel = {
                    navHostController.popBackStack()
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun CropScreen(
    originalBitmap: Bitmap,
    viewModel: CropScreenViewModel,
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
                    viewModel.updateCroppedImage(bitmap)
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
                    viewModel.updateCroppedImage(bitmap)
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

/**
 * Displays a preview of a cropped image and provides actions to accept, retry, or cancel.
 *
 * @param croppedBitmap The [Bitmap] of the cropped image to display.
 * @param onAccept Callback invoked when the user accepts the cropped image.
 * @param onRetry Callback invoked when the user wants to retry the cropping operation.
 * @param onCancel Callback invoked when the user cancels the operation.
 * @param modifier Optional [Modifier] for this composable.
 */
@Composable
fun CroppedImagePreview(
    croppedBitmap: Bitmap,
    onAccept: () -> Unit,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Image preview
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Image(
                bitmap = croppedBitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.cropped_image_preview_description),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        // Action buttons
        CroppedImageActionButtons(
            onAccept = onAccept,
            onRetry = onRetry,
            onCancel = onCancel,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )
    }
}

/**
 * A row of action buttons for the cropped image preview.
 *
 * @param onAccept Callback for the accept action.
 * @param onRetry Callback for the retry action.
 * @param onCancel Callback for the cancel action.
 * @param modifier Optional [Modifier] for this composable.
 */
// Ensure you have the correct Material 3 imports

// ... other necessary imports like Row, Modifier, Color, etc.

@Composable
fun CroppedImageActionButtons(
    onAccept: () -> Unit,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val buttonShape = RoundedCornerShape(50) 

        // Cancel and Retry Buttons
        @Composable
        fun SecondaryButton(
            onClick: () -> Unit,
            imageVector: ImageVector,
            contentDescription: String,
            text: String
        ) {
            OutlinedButton(
                onClick = onClick,
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.surface, shape = buttonShape)
                    .weight(1f)
                    .height(48.dp),
                shape = buttonShape,
                // Use a theme-aware color for the border
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface // Color for text and icon
                )
            ) {
                Icon(imageVector = imageVector, contentDescription = contentDescription)
                Spacer(Modifier.width(8.dp))
                Text(text)
            }
        }

        SecondaryButton(
            onClick = onCancel,
            imageVector = Icons.Filled.Close,
            contentDescription = stringResource(R.string.cancel_action_description),
            text = stringResource(R.string.cancel_button_text)
        )

        SecondaryButton(
            onClick = onRetry,
            imageVector = Icons.Filled.Refresh,
            contentDescription = stringResource(R.string.retry_action_description),
            text = stringResource(R.string.retry_button_text)
        )

        // Accept Button
        Button(
            onClick = onAccept,
            modifier = Modifier
                .weight(1f),
            shape = buttonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = stringResource(R.string.accept_action_description)
            )
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.accept_button_text), style = MaterialTheme.typography.bodySmall)
        }
    }
}
@Preview
@Composable
fun CroppedImageActionButtonsPreview() {
    MaterialTheme {
        CroppedImageActionButtons(
            onAccept = { /* Dummy action */ },
            onRetry = { /* Dummy action */ },
            onCancel = { /* Dummy action */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CroppedImagePreviewPreview() {
    val dummyBitmap = createBitmap(100, 100)
    MaterialTheme {
        CroppedImagePreview(
            croppedBitmap = dummyBitmap,
            onAccept = { /* Dummy action */ },
            onRetry = { /* Dummy action */ },
            onCancel = { /* Dummy action */ }
        )
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
