package com.example.savia_finalproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.savia_finalproject.ui.screen.DashboardScreen
import com.example.savia_finalproject.ui.screen.SplashScreen
import com.example.savia_finalproject.viewmodel.TransactionViewModel
import com.example.savia_finalproject.ui.screen.LoginScreen
import com.example.savia_finalproject.ui.screen.SignUpScreen


@Composable
fun RootNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(onFinished = {
                navController.navigate(Routes.SIGNUP) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                    launchSingleTop = true
                }
            }
            )
        }

        composable(Routes.SIGNUP) {
            SignUpScreen(navController = navController)
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        composable(Routes.DASHBOARD) {
            val vm: TransactionViewModel = viewModel()
            DashboardScreen(
                viewModel = vm,
                navController = navController,
            )
        }
    }
}

