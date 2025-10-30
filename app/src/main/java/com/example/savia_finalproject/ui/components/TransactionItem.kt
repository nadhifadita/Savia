package com.example.savia_finalproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.savia_finalproject.data.model.Transaction
import com.example.savia_finalproject.data.model.TransactionType
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TransactionItem(transaction: Transaction) {
    val greenColor = Color(0xFF4CAF50)
    val redColor = Color(0xFFF44336)
    val numberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ikon Pemasukan/Pengeluaran
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (transaction.type == TransactionType.PEMASUKAN) greenColor.copy(alpha = 0.1f)
                        else redColor.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (transaction.type == TransactionType.PEMASUKAN) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = transaction.type.name,
                    tint = if (transaction.type == TransactionType.PEMASUKAN) greenColor else redColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Deskripsi dan Kategori
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Jumlah (Amount)
            Text(
                text = numberFormat.format(transaction.amount),
                color = if (transaction.type == TransactionType.PEMASUKAN) greenColor else redColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}
