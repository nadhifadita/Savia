package com.example.savia_finalproject
import com.example.savia_finalproject.ui.screen.DashboardScreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.savia_finalproject.ui.theme.Savia_FinalProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Savia_FinalProjectTheme {
                DashboardScreen()
            }
        }
    }
}
