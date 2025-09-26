package com.example.imagetotextandroidapp.ui.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.imagetotextandroidapp.R
import com.example.imagetotextandroidapp.ui.theme.ImageTOTextAndroidAppTheme

@Composable
fun SplashScreen(
    navHostController: NavHostController , splashViewModel: SplashViewModel = hiltViewModel()
) {
    val state = splashViewModel.isSplashDone.collectAsState()
    if (state.value) {
        LaunchedEffect(Unit) {
            navHostController.navigate("ImageExtractor") {
                popUpTo("Splash") { inclusive = true }
            }
        }
    }
     SplashScreenContent()
}

@Composable
fun SplashScreenContent() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.outline_convert_to_text_24),
                contentDescription = "Splash Screen Icon",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.padding(6.dp))
            Text(
                text = "Textify",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "Unlock Text From Your Images",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
@Preview
fun SplashScreenPreview1() {
    ImageTOTextAndroidAppTheme {
        SplashScreen(
            navHostController = NavHostController(LocalContext.current)
        )
    }
}

@Composable
@Preview
fun SplashScreenPreview2() {
    ImageTOTextAndroidAppTheme(
        darkTheme = true
    ) {
        SplashScreen(
            navHostController = NavHostController(LocalContext.current)
        )
    }
}
