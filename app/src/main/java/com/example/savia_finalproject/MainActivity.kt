package com.example.savia_finalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.savia_finalproject.ui.theme.SaviaFinalProjectTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savia_finalproject.ui.screen.DashboardScreen
import com.example.savia_finalproject.viewmodel.TransactionViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SaviaFinalProjectTheme {
                val vm: TransactionViewModel = viewModel()
                DashboardScreen(viewModel = vm)
            }
        }
    }
}