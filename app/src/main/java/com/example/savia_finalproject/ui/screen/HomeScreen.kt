package com.example.savia_finalproject.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.savia_finalproject.ui.components.BalanceCard
import com.example.savia_finalproject.ui.components.BottomNavBar

@Composable
fun DashboardScreen() {
    Scaffold(
        bottomBar = { BottomNavBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier.Companion
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // header
            Text(
                text = "SAVIA",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Companion.Bold),
                color = Color.Companion.Black
            )
            Text(
                text = "Kelola keuangan Anda dengan cerdas",
                color = Color.Companion.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.Companion.height(16.dp))

            // Saldo Card
            BalanceCard()

            Spacer(modifier = Modifier.Companion.height(24.dp))

            // Aksi Cepat
            Text(
                text = "Aksi Cepat",
                fontWeight = FontWeight.Companion.SemiBold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.Companion.height(8.dp))

            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.Companion.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.Companion.width(8.dp))
                Text("Tambah Transaksi")
            }

            Spacer(modifier = Modifier.Companion.height(24.dp))

            // Transaksi Terakhir
            Text(
                text = "Transaksi Terakhir",
                fontWeight = FontWeight.Companion.SemiBold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.Companion.height(8.dp))

            Text(
                text = "Belum ada transaksi",
                color = Color.Companion.Gray,
                fontSize = 14.sp,
                modifier = Modifier.Companion.padding(top = 16.dp)
            )
        }
    }
}