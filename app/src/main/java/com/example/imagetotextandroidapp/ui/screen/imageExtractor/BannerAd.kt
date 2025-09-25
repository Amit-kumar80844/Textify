package com.example.imagetotextandroidapp.ui.screen.imageExtractor

import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun BannerAd(
    adUnitId: String,
    modifier: Modifier = Modifier,
    adSize: AdSize = AdSize.BANNER,
    onAdLoaded: () -> Unit = {},
    onAdFailedToLoad: (LoadAdError) -> Unit = {},
    onAdOpened: () -> Unit = {},
    onAdClosed: () -> Unit = {},
    onAdClicked: () -> Unit = {},
    showPlaceholder: Boolean = true
) {
    var isAdLoaded by remember { mutableStateOf(false) }
    var isAdFailed by remember { mutableStateOf(false) }
    var adView by remember { mutableStateOf<AdView?>(null) }

    val context = LocalContext.current

    // Create AdView
    LaunchedEffect(adUnitId) {
        adView = AdView(context).apply {
            setAdSize(adSize)
            this.adUnitId = adUnitId

            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    isAdLoaded = true
                    isAdFailed = false
                    onAdLoaded()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    isAdLoaded = false
                    isAdFailed = true
                    onAdFailedToLoad(error)
                }

                override fun onAdOpened() {
                    super.onAdOpened()
                    onAdOpened()
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    onAdClosed()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    onAdClicked()
                }
            }

            // Load the ad
            loadAd(AdRequest.Builder().build())
        }
    }

    // Clean up AdView when composable is disposed
    DisposableEffect(adView) {
        onDispose {
            adView?.destroy()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth(0.8f)
            .aspectRatio(16f / 9f),
        contentAlignment = Alignment.Center
    ) {
        when {
            isAdLoaded -> {
                // Show the actual ad
                AndroidView(
                    factory = { adView!! },
                    modifier = Modifier.fillMaxSize()
                )
            }
            isAdFailed && showPlaceholder -> {
                // Show error placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(12.dp)
                        )
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = "Ad failed to load",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ad failed to load",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            showPlaceholder -> {
                // Show loading placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(12.dp)
                        )
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Loading ad...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ImageToTextHomeBanner() {
    BannerAd(
        adUnitId = "ca-app-pub-8392656040320194/8331808479",
        onAdLoaded = {
            Log.d("BannerAd", "Ad loaded successfully")
        },
        onAdFailedToLoad = { error ->
            // Handle ad load failure
            Log.e("BannerAd", "Ad failed to load: ${error.message}")
        },
        onAdClicked = {
            // Handle ad click
            Log.d("BannerAd", "Ad clicked")
        }
    )
}

@Composable
fun CustomBannerAd() {
    BannerAd(
        adUnitId = "ca-app-pub-8392656040320194/8331808479",
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        adSize = AdSize.LARGE_BANNER, // You can use different ad sizes
        showPlaceholder = true
    )
}

@Composable
fun TestBannerAd() {
    BannerAd(
        adUnitId = "ca-app-pub-3940256099942544/6300978111", // Google test banner ID
        onAdLoaded = {
            Log.d("BannerAd", "TEST Ad loaded successfully")
        },
        onAdFailedToLoad = { error ->
            Log.e("BannerAd", "TEST Ad failed: ${error.message}")
        }
    )
}


@Composable
fun BannerWithCallbacks() {
    BannerAd(
        adUnitId = "ca-app-pub-8392656040320194/8331808479",
        onAdLoaded = {
            // This runs when ad successfully loads
            // You can use this to hide loading indicators, track analytics, etc.
            Log.d("BannerAd", "Ad loaded successfully")
        },
        onAdFailedToLoad = { error ->
            // This runs when ad fails to load
            // You can log errors, show fallback content, etc.
            Log.e("BannerAd", "Ad failed to load: ${error.message}")
        },
        onAdClicked = {
            // This runs when user clicks the ad
            // You can track click events, pause music/video, etc.
            Log.d("BannerAd", "Ad clicked")
        },
        onAdOpened = {
            // This runs when ad opens (like when clicking leads to browser)
            Log.d("BannerAd", "Ad opened")
        },
        onAdClosed = {
            // This runs when user returns from ad
            Log.d("BannerAd", "Ad closed")
        }
    )
}