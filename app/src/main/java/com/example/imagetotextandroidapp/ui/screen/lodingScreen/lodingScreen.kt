package com.example.imagetotextandroidapp.ui.screen.lodingScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingScreen(
    modifier : Modifier = Modifier.fillMaxSize(),
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LoadingIndicator(
            modifier = Modifier
                .width(100.dp)
                .height(100.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text("Loading", fontSize = 21.sp)
            var dots by remember { mutableStateOf("") }
            LaunchedEffect(Unit) {
                var count = 0
                while (true) {
                    dots = ".".repeat(count % 4)
                    count++
                    delay(500)
                }
            }
            Text(dots, fontSize = 21.sp)
        }

    }
}

//@Composable
//private fun WavyLoader(
//    modifier: Modifier,
//    primary: Color,
//    background: Color,
//    amplitude: Float,
//    wavelength: Float,
//    phase: Float
//) {
//    val shape = RoundedCornerShape(999.dp)
//
//    Canvas(
//        modifier = modifier.clip(shape)
//    ) {
//        // Background track
//        drawRoundRect(
//            color = background,
//            cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)
//        )
//
//        val w = size.width
//        val h = size.height
//        val centerY = h / 2f
//
//        // Build the sine-wave fill path
//        val path = Path().apply {
//            moveTo(0f, h) // start at bottom-left to create a closed fill
//            val step = 4f // drawing step for performance/quality tradeoff
//            var x = 0f
//            while (x <= w) {
//                val y = centerY + amplitude * sin((2 * PI * (x / wavelength) + phase)).toFloat()
//                lineTo(x, y)
//                x += step
//            }
//            lineTo(w, h)
//            close()
//        }
//
//        // Fill the wave with a subtle gradient for a lively effect
//        val brush = Brush.horizontalGradient(
//            colors = listOf(primary.copy(alpha = 0.85f), primary)
//        )
//
//        drawPath(path = path, brush = brush, style = Fill)
//    }
//}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    LoadingScreen()
}



