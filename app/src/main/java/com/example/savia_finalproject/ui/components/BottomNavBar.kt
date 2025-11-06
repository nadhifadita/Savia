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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.savia_finalproject.ui.navigation.Routes

@Composable
fun BottomNavBar(navController: NavController) {

    var selectedIndex by remember { mutableStateOf(0) }

    NavigationBar {
        val icons = listOf(
            Icons.Default.Home,
            Icons.AutoMirrored.Filled.List,
            Icons.Default.Star,
            Icons.Default.ShoppingCart,
            Icons.Default.Person
        )
        val labels = listOf("dashboard", "transaksi", "Tujuan", "UMKM", "profile")

        icons.forEachIndexed { index, icon ->
            NavigationBarItem(
                selected = index == selectedIndex,
                onClick = {
                    selectedIndex = index
                    navController.navigate(labels[index]) {
                        {
                            // Pop up ke start destination untuk menghindari tumpukan back stack
                            popUpTo(navController.graph.startDestinationId)
                            // Hindari membuat instance baru dari destinasi yang sama
                            launchSingleTop = true
                        }
                    }
                          },
                icon = { Icon(icon, contentDescription = labels[index]) },
                label = { Text(labels[index]) }
            )
        }
    }
}