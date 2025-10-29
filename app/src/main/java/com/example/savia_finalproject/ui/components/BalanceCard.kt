package com.example.savia_finalproject.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun BalanceCard(
    // Terima parameter dari luar
    balance: String,
    income: String,
    expense: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(), // Tidak perlu .Companion
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF247CF0)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp) // Tidak perlu .Companion
        ) {
            Text(
                text = "Saldo Total",
                color = Color.White, // Tidak perlu .Companion
                fontSize = 14.sp
            )
            Text(
                text = balance, // Gunakan parameter
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold) // Tidak perlu .Companion
            )

            Spacer(modifier = Modifier.height(8.dp)) // Tidak perlu .Companion

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth() // Tidak perlu .Companion
            ) {
                Column {
                    Text("Pemasukan", color = Color.White.copy(alpha = 0.7f)) // Tidak perlu .Companion
                    Text(
                        text = income, // Gunakan parameter
                        color = Color.White,
                        fontWeight = FontWeight.Bold // Tidak perlu .Companion
                    )
                }
                Column {
                    Text("Pengeluaran", color = Color.White.copy(alpha = 0.7f))
                    Text(
                        // Gunakan parameter dan Math.abs untuk menghilangkan tanda negatif
                        text = expense,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
