package com.example.imagetotextandroidapp.ui.screen.camera

import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.imagetotextandroidapp.R

@Composable
fun CameraScreen(
    navController: NavHostController
) {
    val viewModel: CameraPreviewViewModel = viewModel()
    CameraPreviewScreen(viewModel,navController)
}

@Composable
fun CameraPreviewScreen(
    viewModel: CameraPreviewViewModel,
    navController: NavHostController
) {
    val controller = LifecycleCameraController(LocalContext.current)
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

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
                    viewModel.captureImage(context, controller,navController)
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
                    painter = painterResource(id = R.drawable.outline_replay_24),
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
