package com.example.savia_finalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.savia_finalproject.ui.navigation.RootNavGraph
import com.example.savia_finalproject.ui.navigation.Routes
import com.example.savia_finalproject.ui.screen.DashboardScreen
import com.example.savia_finalproject.ui.screen.LoginScreen
import com.example.savia_finalproject.ui.screen.SignUpScreen
import com.example.savia_finalproject.ui.screen.SplashScreen
import com.example.savia_finalproject.viewmodel.TransactionViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            RootNavGraph(navController = navController)
        }
    }
}