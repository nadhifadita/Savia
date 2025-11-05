package com.example.savia_finalproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.savia_finalproject.ui.screen.DashboardScreen
import com.example.savia_finalproject.ui.screen.SplashScreen
import com.example.savia_finalproject.viewmodel.TransactionViewModel

@Composable
fun RootNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        // Splash tetap sama
        composable(Routes.SPLASH) {
            SplashScreen(onFinished = {
                navController.navigate(Routes.DASBOARD) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }

        // Ganti: sebelumnya 'navigation(route = MAIN_GRAPH) { ... }'
        // Sekarang: satu composable yang menampilkan MainScaffold
        composable(Routes.DASBOARD) {
            val vm: TransactionViewModel = viewModel()
            DashboardScreen(viewModel = vm)
        }
    }
}