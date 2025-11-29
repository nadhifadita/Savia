package com.example.savia_finalproject.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.savia_finalproject.data.model.Goal
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalBottomSheet(
    onDismiss: () -> Unit,
    onSave: (Goal) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }

    val primaryBlue = Color(0xFF0066FF)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {

        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tambah Goals Keuangan", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "close")
            }
        }

        Spacer(Modifier.height(16.dp))

        // INPUT NAMA GOAL
        SaviaOutlinedField(
            label = "Nama Goals",
            placeholder = "Contoh: Beli Laptop, Dana Darurat",
            value = title,
            onValueChange = { title = it }
        )

        // INPUT TARGET
        SaviaOutlinedField(
            label = "Target Nominal (Rp)",
            placeholder = "0",
            value = targetAmount,
            onValueChange = { targetAmount = it }
        )

        Spacer(Modifier.height(16.dp))

        // SAVE BUTTON
        Button(
            onClick = {
                val goal = Goal(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    targetAmount = targetAmount.toLongOrNull() ?: 0L,
                    savedAmount = 0L,
                    isCompleted = false,
                    createdAt = System.currentTimeMillis()
                )

                onSave(goal)
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(primaryBlue)
        ) {
            Text("Simpan Goal", color = Color.White)
        }

        Spacer(Modifier.height(8.dp))
    }
}
