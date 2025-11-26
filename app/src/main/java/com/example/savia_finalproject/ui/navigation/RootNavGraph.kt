package com.example.savia_finalproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savia_finalproject.ui.screen.*
import com.example.savia_finalproject.viewmodel.TransactionViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RootNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(onFinished = {
                if (FirebaseAuth.getInstance().currentUser != null) {
                    navController.navigate(Routes.DASHBOARD)
                } else {
                    navController.navigate(Routes.LOGIN)
                }
            })
        }

        composable(Routes.SIGNUP) { SignUpScreen(navController = navController) }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        composable(Routes.DASHBOARD) {
            val vm: TransactionViewModel = viewModel()
            DashboardScreen(viewModel = vm, navController = navController)
        }

        composable(Routes.TRANSAKSI) {
            val vm: TransactionViewModel = viewModel()
            TransactionScreen(viewModel = vm, navController = navController)
        }

        // --- TAMBAHAN RUTE BARU ---

        composable(Routes.PROFILE) {
            ProfileScreen(navController = navController)
        }

        composable(Routes.GOALS) {
            GoalScreen(navController = navController)
        }

        composable(Routes.EDUCATION) {
            EducationScreen(navController = navController)
        }
    }
}