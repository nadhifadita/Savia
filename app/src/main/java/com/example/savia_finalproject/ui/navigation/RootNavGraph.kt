package com.example.savia_finalproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savia_finalproject.ui.screen.*
import com.example.savia_finalproject.viewmodel.TransactionViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.savia_finalproject.ui.screen.SplashScreen

@Composable
fun RootNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.SPLASH
    ) {

        composable(Route.SPLASH) {
            SplashScreen(onFinished = {
                if (FirebaseAuth.getInstance().currentUser != null) {
                    navController.navigate(Route.DASHBOARD)
                } else {
                    navController.navigate(Route.LOGIN)
                }
            })
        }

        composable(Route.SIGNUP) { SignUpScreen(navController = navController) }

        composable(Route.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Route.DASHBOARD) {
                        popUpTo(Route.LOGIN) { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        composable(Route.DASHBOARD) {
            val vm: TransactionViewModel = viewModel()
            DashboardScreen(viewModel = vm, navController = navController)
        }

        composable(Route.TRANSAKSI) {
            val vm: TransactionViewModel = viewModel()
            TransactionScreen(viewModel = vm, navController = navController)
        }


        composable(Route.PROFILE) {
            ProfileScreen(navController = navController)
        }

        composable(Route.GOALS) {
            GoalScreen(navController = navController)
        }

        composable(Route.EDUCATION) {
            EducationScreen(navController = navController)
        }
    }
}