package com.example.savia_finalproject.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.savia_finalproject.data.model.Goal

@Composable
fun GoalBottomSheet(
    onDismiss: () -> Unit,
    onSave: (Goal) -> Unit
) {



    var title by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var isTitleEmpty by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tambah Target Baru", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Input untuk Judul
        SaviaOutlinedField(
            label = "Nama Target",
            placeholder = "mis: Beli Mesin Kopi",
            value = title,
            onValueChange = {
                title = it
                // Reset error state when user starts typing
                if (isTitleEmpty) isTitleEmpty = false
            },
            // Pass the isError parameter
            isError = isTitleEmpty,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input untuk Jumlah
        SaviaOutlinedField(
            label = "Jumlah Target",
            placeholder = "mis: 5000000",
            value = targetAmount,
            onValueChange = { targetAmount = it },
            // Pass the isError parameter. For now, we assume it's never in an error state.
            isError = false,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Aksi
        Button(
            onClick = {
                // Set isTitleEmpty to true if title is blank when the button is clicked
                isTitleEmpty = title.isBlank()
                if (!isTitleEmpty) {
                    val newGoalObject = Goal(
                        title = title,
                        targetAmount = targetAmount.toLongOrNull() ?: 0L
                    )
                    onSave(newGoalObject)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0066FF))
        ) {
            Text("Simpan")
        }
    }
}
