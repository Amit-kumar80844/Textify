package com.example.imagetotextandroidapp.ui.screen.imageExtractor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.imagetotextandroidapp.ui.navigation.NavGraph
import com.example.imagetotextandroidapp.ui.screen.crop.SharedViewModel
import com.example.imagetotextandroidapp.ui.screen.lodingScreen.LoadingScreen
import com.example.imagetotextandroidapp.ui.theme.ImageTOTextAndroidAppTheme
import kotlinx.coroutines.delay
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.core.app.ActivityCompat
import android.content.Intent
import android.provider.Settings
import android.net.Uri

@Composable
fun ImageExtractionScreen(
    navHostController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    val viewModel: ImageExtractionViewModel = hiltViewModel()
    val imageState by viewModel.imageState.observeAsState(ImageState.IsLoading)
    val context = LocalContext.current

    LaunchedEffect(key1 = imageState) {
        if (imageState == ImageState.IsLoading) {
            delay(500)
            viewModel.setImageState(ImageState.IsIdle)
        }
    }

    LaunchedEffect(key1 = imageState) {
        when (imageState) {
            is ImageState.IsImageSelected -> {
                val bitmap = viewModel.selectImageUri?.let {
                    viewModel.uriToBitmap(it, context)
                }
                bitmap?.let {
                    sharedViewModel.setImage(it)
                    viewModel.setImageState(ImageState.IsIdle)
                    navHostController.navigate(NavGraph.CropScreen.route)
                }
            }

            is ImageState.IsPreviousText -> {
                viewModel.setImageState(ImageState.IsLoading)
                navHostController.navigate(NavGraph.PreviousText.route)
            }

            else -> Unit // No-op
        }
    }

    // UI Rendering
    when (imageState) {
        is ImageState.IsIdle -> {
            TextExtractorScreen(navHostController, viewModel)
        }

        is ImageState.IsImageSelecting -> {
            PickImage(viewModel = viewModel)
        }

        is ImageState.IsLoading -> {
            LoadingScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        }
        else -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextExtractorScreen(navHostController: NavHostController, viewModel: ImageExtractionViewModel) {
    // Camera permission state
    var hasCameraPermission by remember { mutableStateOf(false) }
    var showRationale by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? androidx.activity.ComponentActivity

    // Check initial permission state
    LaunchedEffect(Unit) {
        hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            // Navigate to camera preview if permission is granted
            showRationale = false
            navHostController.navigate(NavGraph.CameraPreview.route)
        } else {
            // Check if we should show rationale or if permission is permanently denied
            val shouldShowRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    it,
                    android.Manifest.permission.CAMERA
                )
            } ?: false

            if (shouldShowRationale) {
                showRationale = true
            } else {
                // Permission permanently denied, show settings dialog
                showSettingsDialog = true
            }
        }
    }

    // Function to handle camera button click
    fun handleCameraClick() {
        when {
            hasCameraPermission -> {
                // Permission already granted, navigate to camera
                navHostController.navigate(NavGraph.CameraPreview.route)
            }
            else -> {
                // Check if we should show rationale first
                val shouldShowRationale = activity?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        it,
                        android.Manifest.permission.CAMERA
                    )
                } ?: false

                if (shouldShowRationale) {
                    showRationale = true
                } else {
                    // Request permission directly
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    "Text Extractor",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select an image from your gallery or take a new photo to extract text.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    onClick = {
                        viewModel.setImageState(ImageState.IsImageSelecting)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(0.8f)
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Select Image",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 18.sp
                    )
                }

                OutlinedButton(
                    onClick = { handleCameraClick() },
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
            TestBannerAd()

            Spacer(modifier = Modifier.height(24.dp))

            // Prev Extracted Text Button
            Button(
                onClick = {
                    viewModel.setImageState(ImageState.IsPreviousText)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(0.8f)
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(" Previous Text", color = MaterialTheme.colorScheme.onPrimary, fontSize = 16.sp)
            }
        }
    }

    // Permission Rationale Dialog
    if (showRationale) {
        AlertDialog(
            onDismissRequest = { showRationale = false },
            title = {
                Text(
                    "Camera Permission Required",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "This app needs camera permission to take photos for text extraction. Please grant the permission to continue.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRationale = false
                        permissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                ) {
                    Text(
                        "Grant Permission",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRationale = false }
                ) {
                    Text(
                        "Cancel",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        )
    }

    // Settings Dialog (when permission is permanently denied)
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Text(
                    "Permission Permanently Denied",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Camera permission has been permanently denied. Please enable it manually in Settings > Apps > [App Name] > Permissions.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSettingsDialog = false
                        // Open app settings
                        try {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            // Fallback to general settings if specific app settings fail
                            context.startActivity(Intent(Settings.ACTION_SETTINGS))
                        }
                    }
                ) {
                    Text(
                        "Open Settings",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSettingsDialog = false }
                ) {
                    Text(
                        "Cancel",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        )
    }
}
@Composable
fun PickImage(
    viewModel: ImageExtractionViewModel
) {
    var loading by remember { mutableStateOf(false) }
    LaunchedEffect(
        Unit
    ) {
        loading = true
        delay(1300)
        loading = false
    }
    if (loading) {
        LoadingScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
    }
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.selectImageUri = uri
                viewModel.setImageState(ImageState.IsImageSelected)
            } else {
                // User cancelled, go back to idle state
                viewModel.setImageState(ImageState.IsIdle)
            }
        }
    )
    LaunchedEffect(Unit) {
        singlePhotoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }
}

@Composable
@Preview
fun AppPreview() {
    ImageTOTextAndroidAppTheme {
        TextExtractorScreen(NavHostController(LocalContext.current), hiltViewModel())
    }
}