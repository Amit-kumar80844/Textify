package com.example.imagetotextandroidapp.ui.screen.camera

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.imagetotextandroidapp.data.image.bitmapToFile
import com.yalantis.ucrop.UCrop
import java.io.File

@Composable
fun StartCroppingUI(
    bitmap: Bitmap,
    onCropped: (Bitmap) -> Unit,
    onError: (String) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val context = LocalContext.current
    var isLaunched by remember { mutableStateOf(false) }

    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                try {
                    val resultUri = UCrop.getOutput(result.data!!)
                    if (resultUri != null) {
                        val croppedBitmap = MediaStore.Images.Media.getBitmap(
                            context.contentResolver,
                            resultUri
                        )
                        onCropped(croppedBitmap)

                        // Clean up temp file
                        context.contentResolver.delete(resultUri, null, null)
                    } else {
                        onError("Failed to get cropped image")
                    }
                } catch (e: Exception) {
                    Log.e("CropError", "Error processing cropped image", e)
                    onError("Error processing cropped image: ${e.message}")
                }
            }
            Activity.RESULT_CANCELED -> {
                onCancel()
            }
            UCrop.RESULT_ERROR -> {
                val cropError = UCrop.getError(result.data!!)
                Log.e("CropError", "UCrop error", cropError)
                onError("Crop failed: ${cropError?.message ?: "Unknown error"}")
            }
        }
    }

    LaunchedEffect(bitmap) {
        if (!isLaunched) {
            isLaunched = true
            try {
                val inputUri = bitmapToFile(context, bitmap)
                val outputFile = File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
                val outputUri = Uri.fromFile(outputFile)

                val uCropOptions = UCrop.Options().apply {
                    // UI customizations
                    setFreeStyleCropEnabled(true)
                    setHideBottomControls(false)
                    setShowCropFrame(true)
                    setShowCropGrid(true)

                    // Quality settings
                    setCompressionQuality(90)
                    setMaxBitmapSize(2048)

                    // Colors (optional - customize to match your app theme)
                    setToolbarColor(Color.BLACK)
                    setStatusBarColor(Color.BLACK)
                    setActiveControlsWidgetColor(Color.WHITE)
                }

                val intent = UCrop.of(inputUri, outputUri)
                    .withOptions(uCropOptions)
                    .getIntent(context)

                cropLauncher.launch(intent)
            } catch (e: Exception) {
                Log.e("CropError", "Error starting crop", e)
                onError("Failed to start crop: ${e.message}")
            }
        }
    }

    // Loading UI while crop is opening
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Opening Crop Editor...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
