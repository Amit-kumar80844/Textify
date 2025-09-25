package com.example.imagetotextandroidapp.ui.screen.camera

import android.content.Context
import android.util.Log
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.imagetotextandroidapp.R
import com.example.imagetotextandroidapp.ui.screen.crop.SharedViewModel
import kotlinx.coroutines.delay

@Composable
fun CameraScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    val viewModel: CameraPreviewViewModel = hiltViewModel()
    var loading by remember{ mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(500)
        loading = false
    }
    if(loading){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }else {
        CameraPreviewScreen(viewModel, sharedViewModel, navController)
    }
}

@Composable
fun CameraPreviewScreen(
    viewModel: CameraPreviewViewModel,
    sharedViewModel: SharedViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // State management
    var isCapturing by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Safely create controller with error handling
    val controller = remember {
        try {
            LifecycleCameraController(context).apply {
                // Set initial configuration
                setEnabledUseCases(
                    LifecycleCameraController.IMAGE_CAPTURE or
                            LifecycleCameraController.IMAGE_ANALYSIS
                )
            }
        } catch (e: Exception) {
            Log.e("CameraScreen", "Failed to create camera controller", e)
            null
        }
    }

    // Handle camera initialization
    LaunchedEffect(controller) {
        controller?.let { safeController ->
            try {
                safeController.bindToLifecycle(lifecycleOwner)
                hasError = false
            } catch (e: Exception) {
                Log.e("CameraScreen", "Failed to bind camera to lifecycle", e)
                hasError = true
                errorMessage = "Camera initialization failed: ${e.localizedMessage}"
            }
        } ?: run {
            hasError = true
            errorMessage = "Camera controller creation failed"
        }
    }

    // Cleanup on dispose
    DisposableEffect(controller) {
        onDispose {
            try {
                controller?.unbind()
            } catch (e: Exception) {
                Log.w("CameraScreen", "Error during camera cleanup", e)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            hasError -> {
                // Error state UI
                ErrorScreen(
                    message = errorMessage,
                    onRetry = {
                        hasError = false
                        errorMessage = ""
                    },
                    onCancel = {
                        safeNavigateBack(navController)
                    }
                )
            }

            controller != null -> {
                // Main camera UI
                CameraContent(
                    controller = controller,
                    isCapturing = isCapturing,
                    onCaptureClick = {
                        if (!isCapturing) {
                            isCapturing = true
                            safeCaptureImage(
                                viewModel = viewModel,
                                context = context,
                                controller = controller,
                                navController = navController,
                                sharedViewModel = sharedViewModel,
                                onComplete = { isCapturing = false },
                                onError = { error ->
                                    isCapturing = false
                                    hasError = true
                                    errorMessage = error
                                }
                            )
                        }
                    },
                    onCancelClick = {
                        if (!isCapturing) {
                            try {
                                sharedViewModel.clearImage()
                                safeNavigateBack(navController)
                            } catch (e: Exception) {
                                Log.e("CameraScreen", "Error during cancel", e)
                            }
                        }
                    }
                )
            }

            else -> {
                // Loading state
                LoadingScreen()
            }
        }
    }
}

@Composable
private fun CameraContent(
    controller: LifecycleCameraController,
    isCapturing: Boolean,
    onCaptureClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    Column(modifier = Modifier.fillMaxSize()) {
        // Camera preview with error boundary
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(12.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            CameraPreviewView(
                controller = controller,
                modifier = Modifier.fillMaxSize(),
                lifecycleOwner = lifecycleOwner
            )

            // Capture indicator overlay
            if (isCapturing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }

        // Bottom controls
        CameraControls(
            isCapturing = isCapturing,
            onCaptureClick = onCaptureClick,
            onCancelClick = onCancelClick
        )
    }
}

@Composable
private fun CameraControls(
    isCapturing: Boolean,
    onCaptureClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cancel button
        IconButton(
            onClick = onCancelClick,
            enabled = !isCapturing,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    if (isCapturing)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    else
                        MaterialTheme.colorScheme.errorContainer
                )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_cancel_24),
                contentDescription = "Cancel",
                modifier = Modifier.size(40.dp),
                tint = if (isCapturing)
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                else
                    MaterialTheme.colorScheme.onErrorContainer
            )
        }

        // Capture button
        IconButton(
            onClick = onCaptureClick,
            enabled = !isCapturing,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    if (isCapturing)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    else
                        MaterialTheme.colorScheme.primaryContainer
                )
        ) {
            if (isCapturing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.outline_photo_camera_24),
                    contentDescription = "Capture Image",
                    modifier = Modifier.size(50.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.outline_error_24),
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Camera Error",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(onClick = onCancel) {
                Text("Go Back")
            }

            Button(onClick = onRetry) {
                Text("Try Again")
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Initializing Camera...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun safeCaptureImage(
    viewModel: CameraPreviewViewModel,
    context: Context,
    controller: LifecycleCameraController,
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    onComplete: () -> Unit,
    onError: (String) -> Unit
) {
    try {
        Log.d("CameraScreen", "Starting safe image capture")
        viewModel.captureImage(context, controller, navController) { bitmap ->
            try {
                bitmap?.let {
                    sharedViewModel.setImage(it)
                    Log.d("CameraScreen", "Image captured and set successfully")
                } ?: run {
                    onError("Failed to capture image - bitmap is null")
                }
            } catch (e: Exception) {
                Log.e("CameraScreen", "Error setting image in shared view model", e)
                onError("Failed to process captured image: ${e.localizedMessage}")
            } finally {
                onComplete()
            }
        }
    } catch (e: Exception) {
        Log.e("CameraScreen", "Error during image capture", e)
        onError("Capture failed: ${e.localizedMessage}")
        onComplete()
    }
}

private fun safeNavigateBack(navController: NavHostController) {
    try {
        if (navController.currentBackStackEntry != null) {
            navController.popBackStack()
        }
    } catch (e: Exception) {
        Log.e("CameraScreen", "Navigation error", e)
    }
}

@Composable
fun CameraPreviewView(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner
) {

    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun CameraScreenPreview() {

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            // Placeholder for camera preview
            Text(
                text = "📷 Camera Preview",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(onClick = {
                Log.d("CameraScreenPreview", "Capture button clicked")
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_photo_camera_24),
                    contentDescription = "Capture",
                    modifier = Modifier
                        .size(130.dp)
                        .background(color = MaterialTheme.colorScheme.onPrimary)
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_cancel_24),
                    contentDescription = "Cancel",
                    modifier = Modifier
                        .size(130.dp)
                        .background(color = MaterialTheme.colorScheme.onPrimary)
                )
            }
        }
    }
}