package com.example.savia_finalproject.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
        Text("Tambah Target Baru", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // Input untuk Judul
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                isTitleEmpty = it.isEmpty()
            },
            label = { Text("Nama Target (mis: Beli Mesin Kopi)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = isTitleEmpty
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input untuk Jumlah
        OutlinedTextField(
            value = targetAmount,
            onValueChange = { if (it.all { char -> char.isDigit() }) targetAmount = it },
            label = { Text("Jumlah Target (mis: 5000000)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Aksi
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onDismiss) { Text("Batal") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val newGoalObject = Goal(
                            title = title,
                            targetAmount = targetAmount.toLongOrNull() ?: 0L
                        )
                        onSave(newGoalObject)
                    } else {
                        isTitleEmpty = true
                    }
                }
            ) {
                Text("Simpan")
            }
        }
    }
}
