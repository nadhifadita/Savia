package com.example.savia_finalproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BalanceCard(
    balance: String,
    income: String,
    expense: String
) {
    // Definisi Gradient Modern (Biru ke Ungu muda)
    val brush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF0061FF), // Biru terang
            Color(0xFF60EFFF)  // Cyan cerah
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp), // Sedikit lebih tinggi agar lega
        shape = RoundedCornerShape(24.dp), // Radius lebih membulat
        elevation = CardDefaults.cardElevation(8.dp) // Shadow lebih menonjol
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush) // Pakai gradient
                .padding(24.dp)
        ) {
            // Hiasan lingkaran transparan di background (opsional, untuk estetika)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 20.dp, y = (-20).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            )

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                // Bagian Saldo Utama
                Column {
                    Text(
                        text = "Total Saldo",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = balance,
                        color = Color.White,
                        fontSize = 32.sp, // Font lebih besar
                        fontWeight = FontWeight.Bold
                    )
                }

                // Bagian Income & Expense
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FinanceIndicator(
                        label = "Pemasukan",
                        amount = income,
                        icon = Icons.Default.ArrowUpward,
                        iconColor = Color(0xFF4CAF50) // Hijau
                    )

                    // Garis pemisah vertikal tipis
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(Color.White.copy(alpha = 0.3f))
                            .align(Alignment.CenterVertically)
                    )

                    FinanceIndicator(
                        label = "Pengeluaran",
                        amount = expense,
                        icon = Icons.Default.ArrowDownward,
                        iconColor = Color(0xFFFF5252)
                    )
                }
            }
        }
    }
}

@Composable
fun FinanceIndicator(
    label: String,
    amount: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color.White, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp
            )
            Text(
                text = amount,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}