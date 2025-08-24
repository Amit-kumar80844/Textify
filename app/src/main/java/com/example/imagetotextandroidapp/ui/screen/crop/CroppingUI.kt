package com.example.imagetotextandroidapp.ui.screen.crop

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.imagetotextandroidapp.data.image.bitmapToFile
import com.yalantis.ucrop.UCrop
import java.io.File

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun StartCroppingUI(
    bitmap: Bitmap,
    onCropped: (Bitmap) -> Unit,
    onError: (String) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val context = LocalContext.current

    // Theme colors for UCrop
    val toolbarColor = MaterialTheme.colorScheme.primary.toArgb()
    val statusBarColor = MaterialTheme.colorScheme.primaryContainer.toArgb()
    val activeControlsWidgetColor = MaterialTheme.colorScheme.primary.toArgb()
    val toolbarWidgetColor = MaterialTheme.colorScheme.onPrimary.toArgb()
    val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let { intent ->
                    UCrop.getOutput(intent)?.let { resultUri ->
                        try {
                            val source = ImageDecoder.createSource(context.contentResolver, resultUri)
                            val croppedBitmap = ImageDecoder.decodeBitmap(source)
                            onCropped(croppedBitmap)

                            // cleanup cropped temp file
                            context.contentResolver.delete(resultUri, null, null)
                        } catch (e: Exception) {
                            Log.e("CropResultError", "Error decoding cropped image", e)
                            onError("Error processing cropped image: ${e.message}")
                        }
                    } ?: onError("Failed to retrieve cropped image URI.")
                } ?: onError("No data returned from crop activity.")
            }
            Activity.RESULT_CANCELED -> {
                onCancel()
            }
            UCrop.RESULT_ERROR -> {
                val cropError = result.data?.let { UCrop.getError(it) }
                Log.e("UCropError", "UCrop error", cropError)
                onError("Crop failed: ${cropError?.message ?: "Unknown error"}")
            }
        }
    }

    LaunchedEffect(bitmap) {
        try {
            val inputUri = bitmapToFile(context, bitmap)

            val outputFile = File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
            val outputUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                outputFile
            )

            val uCropOptions = UCrop.Options().apply {
                setFreeStyleCropEnabled(true)
                setHideBottomControls(false)
                setShowCropFrame(true)
                setShowCropGrid(true)

                setCompressionQuality(90)
                setMaxBitmapSize(2048)
                setToolbarColor(toolbarColor)
                setStatusBarColor(statusBarColor)
                setActiveControlsWidgetColor(activeControlsWidgetColor)
                setToolbarWidgetColor(toolbarWidgetColor)
                setRootViewBackgroundColor(backgroundColor)
                setToolbarTitle("Crop Image")
            }

            val intent = UCrop.of(inputUri, outputUri)
                .withOptions(uCropOptions)
                .getIntent(context)

            cropLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e("CropStartError", "Error starting crop activity", e)
            onError("Failed to start crop: ${e.message}")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Opening Crop Editor...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
