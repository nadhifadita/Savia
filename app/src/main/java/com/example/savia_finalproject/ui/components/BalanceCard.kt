package com.example.savia_finalproject.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun BalanceCard() {
    Card(
        modifier = Modifier.Companion.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF247CF0)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.Companion.padding(20.dp)
        ) {
            Text(
                text = "Saldo Total",
                color = Color.Companion.White,
                fontSize = 14.sp
            )
            Text(
                text = "Rp 0",
                color = Color.Companion.White,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Companion.Bold)
            )

            Spacer(modifier = Modifier.Companion.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.Companion.fillMaxWidth()
            ) {
                Column {
                    Text("Pemasukan", color = Color.Companion.White.copy(alpha = 0.7f))
                    Text(
                        "Rp 0",
                        color = Color.Companion.White,
                        fontWeight = FontWeight.Companion.Bold
                    )
                }
                Column {
                    Text("Pengeluaran", color = Color.Companion.White.copy(alpha = 0.7f))
                    Text(
                        "Rp 0",
                        color = Color.Companion.White,
                        fontWeight = FontWeight.Companion.Bold
                    )
                }
            }
        }
    }
}