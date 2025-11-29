package com.example.savia_finalproject.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.savia_finalproject.data.model.Transaction
import com.example.savia_finalproject.data.model.TransactionType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionBottomSheet(
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit
) {
    var type by remember { mutableStateOf(TransactionType.PENGELUARAN) }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }


    var selectedDate by remember { mutableStateOf(Date()) }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    var formattedDate by remember(selectedDate) {
        mutableStateOf(dateFormat.format(selectedDate))
    }

    val showDatePicker = remember { mutableStateOf(false) }

    val incomeCategories = listOf("Gaji", "Bonus", "Freelance", "Investasi", "Lainnya")
    val expenseCategories = listOf(
        "Makanan", "Transportasi", "Utilitas", "Hiburan", "Kesehatan",
        "Pendidikan", "Belanja", "Kebutuhan Rumah", "Lainnya"
    )

    val categoryList = if (type == TransactionType.PEMASUKAN) incomeCategories else expenseCategories

    var expanded by remember { mutableStateOf(false) }
    val primaryBlue = Color(0xFF0066FF)

    if (showDatePicker.value) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.time,
            yearRange = 2000..2030 // Sesuaikan rentang tahun jika perlu
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker.value = false
                        // Ambil tanggal pilihan (epoch millis) dan ubah ke objek Date
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Date(millis)
                        }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker.value = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Header row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Tambah Transaksi", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Type toggle
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val inactive = Color(0xFFE5E7EB)
            Button(
                onClick = { type = TransactionType.PEMASUKAN; category = "" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (type == TransactionType.PEMASUKAN) primaryBlue else inactive,
                    contentColor = if (type == TransactionType.PEMASUKAN) Color.White else Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) { Text("Pemasukan") }

            Button(
                onClick = { type = TransactionType.PENGELUARAN; category = "" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (type == TransactionType.PENGELUARAN) primaryBlue else inactive,
                    contentColor = if (type == TransactionType.PENGELUARAN) Color.White else Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) { Text("Pengeluaran") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Inputs
        SaviaOutlinedField(
            label = "Deskripsi",
            placeholder = "Contoh: Gaji, Makan siang",
            value = description,
            onValueChange = { description = it }
        )

        SaviaOutlinedField(
            label = "Jumlah (Rp)",
            placeholder = "0",
            value = amount,
            onValueChange = { amount = it }
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Kategori") },
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(10.dp)
            )

            ExposedDropdownMenuBoxScopeLocalizedMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                items = categoryList,
                onItemSelected = { sel ->
                    category = sel
                    expanded = false
                }
            )
        }

        Box {
            SaviaOutlinedField(
                label = "Tanggal",
                placeholder = formattedDate,
                value = formattedDate,
                onValueChange = {},
                readOnly = true
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(onClick = { showDatePicker.value = true })
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save button
        Button(
            onClick = {
                val rawAmount = amount.replace(",", "").toDoubleOrNull() ?: 0.0
                val finalAmount = if (type == TransactionType.PENGELUARAN) -abs(rawAmount) else abs(rawAmount)

                val tx = Transaction(
                    type = type,
                    description = description,
                    amount = finalAmount,
                    category = category.ifEmpty { if (type == TransactionType.PEMASUKAN) "Lainnya" else "Lainnya" },
                    date = selectedDate
                )

                val auth = FirebaseAuth.getInstance()
                val db = FirebaseFirestore.getInstance()
                val userId = auth.currentUser?.uid

                if (userId != null) {
                    val userRef = db.collection("users").document(userId)

                    db.runTransaction { transaction ->
                        val snapshot = transaction.get(userRef)

                        // Ambil saldo lama (default 0 jika belum ada)
                        val oldBalance = snapshot.getDouble("balance") ?: 0.0
                        val newBalance = oldBalance + finalAmount

                        // Ambil transaksi lama, lalu tambahkan transaksi baru
                        val oldTransactions = snapshot.get("transactions") as? List<Map<String, Any>> ?: emptyList()

                        val newTransaction = mapOf(
                            "type" to tx.type.name,
                            "description" to tx.description,
                            "amount" to tx.amount,
                            "category" to tx.category,
                            "date" to tx.date.time // simpan timestamp
                        )

                        val updatedTransactions = oldTransactions + newTransaction

                        // Update balance dan transaksi di Firestore
                        transaction.update(userRef, mapOf(
                            "balance" to newBalance,
                            "transactions" to updatedTransactions
                        ))
                    }.addOnSuccessListener {
                        onSave(tx)
                        onDismiss()
                    }.addOnFailureListener { e ->
                        Log.e("Firestore", "Gagal menyimpan transaksi", e)
                    }
                } else {
                    Log.e("Firestore", "User belum login")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
        ) {
            Text(text = "Simpan Transaksi", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Composable
private fun ExposedDropdownMenuBoxScopeLocalizedMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        items.forEach { option ->
            DropdownMenuItem(text = { Text(option) }, onClick = { onItemSelected(option) })
        }
    }
}
