package com.example.savia_finalproject.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Book // Icon untuk Edukasi
import androidx.compose.material.icons.filled.Flag // Icon untuk Goals/Tujuan
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.savia_finalproject.ui.navigation.Route

private val BluePrimary = Color(0xFF0052D4)

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        contentColor = BluePrimary
    ) {
        val items = listOf(
            Triple(Route.DASHBOARD, "Beranda", Icons.Default.Home),
            Triple(Route.TRANSAKSI, "transaksi", Icons.AutoMirrored.Filled.List),
            Triple(Route.GOALS, "Target", Icons.Default.Flag), // Fitur Target Tabungan
            Triple(Route.EDUCATION, "Edukasi", Icons.Default.Book), // Fitur Edukasi
            Triple(Route.PROFILE, "Profil", Icons.Default.Person)
        )

        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(Route.DASHBOARD) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BluePrimary,
                    selectedTextColor = BluePrimary,
                    indicatorColor = Color(0xFFE3F2FD), // Biru sangat muda
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

