package com.example.imagetotextandroidapp.ui.screen.camera

import android.util.Log
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.imagetotextandroidapp.R

@Composable
fun CameraScreen(
    navController: NavHostController
) {
    val viewModel: CameraPreviewViewModel = hiltViewModel()
    CameraPreviewScreen(viewModel, navController)
}

@Composable
fun CameraPreviewScreen(
    viewModel: CameraPreviewViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val controller = remember { LifecycleCameraController(context) }

    LaunchedEffect(Unit) {
        controller.setEnabledUseCases(
            LifecycleCameraController.IMAGE_CAPTURE or
                    LifecycleCameraController.IMAGE_ANALYSIS
        )
        controller.bindToLifecycle(lifecycleOwner)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreviewView(controller, Modifier.fillMaxSize(), lifecycleOwner)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(
                onClick = {
                    Log.d("CameraScreen", "Camera Button clicked")
                    viewModel.captureImage(context, controller, navController)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_photo_camera_24),
                    contentDescription = "Capture Image",
                    Modifier.size(130.dp)
                )
            }
            IconButton(
                onClick = {
                    viewModel.cancelCamera(navController)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_cancel_24),
                    contentDescription = "Cancel",
                    Modifier.size(130.dp)
                )
            }
        }
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
                    modifier = Modifier.size(130.dp)
                        .background(color = MaterialTheme.colorScheme.onPrimary)
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_cancel_24),
                    contentDescription = "Cancel",
                    modifier = Modifier.size(130.dp)
                        .background(color = MaterialTheme.colorScheme.onPrimary)
                )
            }
        }
    }
}