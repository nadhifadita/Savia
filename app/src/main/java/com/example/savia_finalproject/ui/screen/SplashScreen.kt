package com.example.savia_finalproject.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.savia_finalproject.R

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    delayMillis: Long = 2000L
) {
    // Jalankan efek sekali: delay lalu panggil onFinished()
    LaunchedEffect(Unit) {
        delay(delayMillis)
        onFinished()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo aplikasi",
                modifier = Modifier.size(300.dp)
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun SplashPreview() {
    SplashScreen(onFinished = {})
}
