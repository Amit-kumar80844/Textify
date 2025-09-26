package com.example.imagetotextandroidapp.ui.screen.imageExtractor

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.*
import kotlinx.coroutines.delay

enum class AdState {
    LOADING,
    LOADED,
    FAILED,
    HIDDEN
}

/**
 * Enhanced BannerAd composable with better UX and error handling
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BannerAdComposable(
    adUnitId: String,
    modifier: Modifier = Modifier,
    adSize: AdSize = AdSize.BANNER,
    showPlaceholder: Boolean = true,
    enableRetry: Boolean = true,
    autoRetryDelay: Long = 30000L, // 30 seconds
    maxRetries: Int = 3,
    isDismissible: Boolean = false,
    cornerRadius: Int = 12,
    onAdLoaded: () -> Unit = {},
    onAdFailedToLoad: (LoadAdError) -> Unit = {},
    onAdOpened: () -> Unit = {},
    onAdClosed: () -> Unit = {},
    onAdClicked: () -> Unit = {},
    onAdDismissed: () -> Unit = {}
) {
    val context = LocalContext.current
    val isInPreview = LocalInspectionMode.current

    var adState by remember { mutableStateOf(AdState.LOADING) }
    var retryCount by remember { mutableIntStateOf(0) }
    var lastError by remember { mutableStateOf<LoadAdError?>(null) }

    // Create AdView only once and handle recreation
    var adView by remember { mutableStateOf<AdView?>(null) }

    // Function to create/recreate AdView
    fun createAdView(): AdView {
        adView?.destroy() // Clean up previous instance

        return AdView(context).apply {
            setAdSize(adSize)
            this.adUnitId = adUnitId
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    adState = AdState.LOADED
                    retryCount = 0
                    lastError = null
                    Log.d("BannerAd", "Ad loaded successfully: $adUnitId")
                    onAdLoaded()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    adState = AdState.FAILED
                    lastError = error
                    Log.e("BannerAd", "Ad failed to load: ${error.message} (Code: ${error.code})")
                    onAdFailedToLoad(error)
                }

                override fun onAdOpened() {
                    super.onAdOpened()
                    Log.d("BannerAd", "Ad opened")
                    onAdOpened()
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    Log.d("BannerAd", "Ad closed")
                    onAdClosed()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    Log.d("BannerAd", "Ad clicked")
                    onAdClicked()
                }
            }
        }.also { newAdView ->
            adView = newAdView
        }
    }

    // Function to load ad
    fun loadAd() {
        if (isInPreview) return

        val currentAdView = adView ?: createAdView()
        adState = AdState.LOADING
        currentAdView.loadAd(AdRequest.Builder().build())
    }

    // Function to retry loading ad
    fun retryAd() {
        if (retryCount < maxRetries) {
            retryCount++
            Log.d("BannerAd", "Retrying ad load (attempt $retryCount/$maxRetries)")
            createAdView() // Create new instance for retry
            loadAd()
        } else {
            Log.w("BannerAd", "Max retry attempts reached for ad: $adUnitId")
        }
    }

    // Auto-retry effect
    LaunchedEffect(adState, retryCount) {
        if (adState == AdState.FAILED && enableRetry && retryCount < maxRetries) {
            delay(autoRetryDelay)
            retryAd()
        }
    }

    // Initial load
    LaunchedEffect(adUnitId) {
        if (!isInPreview) {
            createAdView()
            loadAd()
        }
    }

    // Dispose AdView when Composable is removed
    DisposableEffect(Unit) {
        onDispose {
            adView?.destroy()
        }
    }

    // Don't show anything if hidden or in preview mode without placeholder
    if (adState == AdState.HIDDEN || (isInPreview && !showPlaceholder)) {
        return
    }

    // Calculate aspect ratio based on ad size
    val aspectRatio = remember(adSize) {
        when (adSize) {
            AdSize.BANNER, AdSize.LARGE_BANNER -> 320f / 50f
            AdSize.MEDIUM_RECTANGLE -> 300f / 250f
            AdSize.LEADERBOARD -> 728f / 90f
            else -> 320f / 50f
        }
    }

    AnimatedVisibility(
        visible = adState != AdState.HIDDEN,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(300)
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300))
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio),
            shape = RoundedCornerShape(cornerRadius.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (adState) {
                    AdState.LOADED -> {
                        adView?.let { view ->
                            AndroidView(
                                factory = { view },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    AdState.FAILED -> {
                        if (showPlaceholder) {
                            AdErrorPlaceholder(
                                error = lastError,
                                retryCount = retryCount,
                                maxRetries = maxRetries,
                                enableRetry = enableRetry,
                                onRetry = ::retryAd,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    AdState.LOADING -> {
                        if (showPlaceholder) {
                            AdLoadingPlaceholder(
                                modifier = Modifier.fillMaxSize(),
                                isPreview = isInPreview
                            )
                        }
                    }

                    AdState.HIDDEN -> {
                        // Nothing to show
                    }
                }

                // Dismiss button
                if (isDismissible && adState != AdState.HIDDEN) {
                    IconButton(
                        onClick = {
                            adState = AdState.HIDDEN
                            onAdDismissed()
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .background(
                                Color.Black.copy(alpha = 0.6f),
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Dismiss ad",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdLoadingPlaceholder(
    modifier: Modifier = Modifier,
    isPreview: Boolean = false
) {
    Box(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isPreview) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
            Text(
                text = if (isPreview) "Ad Preview" else "Loading ad...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isPreview) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun AdErrorPlaceholder(
    error: LoadAdError?,
    retryCount: Int,
    maxRetries: Int,
    enableRetry: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.error.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Filled.Warning,
                contentDescription = "Ad failed",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "Ad failed to load",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium
            )

            if (error != null && error.code != AdRequest.ERROR_CODE_NO_FILL) {
                Text(
                    "Code: ${error.code}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (enableRetry && retryCount < maxRetries) {
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedButton(
                    onClick = onRetry,
                    modifier = Modifier.height(28.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Retry",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                if (retryCount > 0) {
                    Text(
                        "Attempt $retryCount/$maxRetries",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/*
*
 * Predefined banner ad configurations
object BannerAdDefaults {
    @Composable
    fun TestBannerAd(
        modifier: Modifier = Modifier,
        isDismissible: Boolean = false,
        onAdInteraction: (String) -> Unit = {}
    ) {
        BannerAdComposable(
            adUnitId = "ca-app-pub-3940256099942544/6300978111", // Google test ID
            modifier = modifier,
            isDismissible = isDismissible,
            onAdLoaded = { onAdInteraction("Test ad loaded") },
            onAdFailedToLoad = { error -> onAdInteraction("Test ad failed: ${error.message}") },
            onAdClicked = { onAdInteraction("Test ad clicked") }
        )
    }

    @Composable
    fun ProductionBannerAd(
        modifier: Modifier = Modifier.fillMaxWidth(),
        isDismissible: Boolean = false,
        onAdInteraction: (String) -> Unit = {}
    ) {
        BannerAdComposable(
            adUnitId = "ca-app-pub-8392656040320194/8331808479",
            modifier = modifier,
            isDismissible = isDismissible,
            maxRetries = 5,
            autoRetryDelay = 10000L,
            onAdLoaded = { onAdInteraction("Production ad loaded") },
            onAdFailedToLoad = { error -> onAdInteraction("Production ad failed: ${error.message}") },
            onAdClicked = { onAdInteraction("Production ad clicked") }
        )
    }
}*/
