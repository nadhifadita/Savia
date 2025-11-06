package com.example.savia_finalproject.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.savia_finalproject.ui.navigation.Routes

@Composable
fun BottomNavBar(navController: NavController) {
    NavigationBar {
        val icons = listOf(
            Icons.Default.Home,
            Icons.AutoMirrored.Filled.List,
            Icons.Default.Star,
            Icons.Default.ShoppingCart,
            Icons.Default.Person
        )
        val labels = listOf("Dashboard", "Transaksi", "Tujuan", "UMKM", "Profil")

        icons.forEachIndexed { index, icon ->
            NavigationBarItem(
                selected = index == 4,
                onClick = { navController.navigate(Routes.PROFILE) },
                icon = { Icon(icon, contentDescription = labels[index]) },
                label = { Text(labels[index]) }
            )
        }
    }
}