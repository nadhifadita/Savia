package com.example.savia_finalproject.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.savia_finalproject.data.model.Transaction
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TransactionItem(tx: Transaction) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = tx.description)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Rp ${tx.amount.toInt()} • ${tx.category}")
        }
    }
}
