package com.example.savia_finalproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.savia_finalproject.data.model.Transaction
import com.example.savia_finalproject.data.model.TransactionType
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

    val date = remember { Date() }
    val formattedDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date) }

    val incomeCategories = listOf("Gaji", "Bonus", "Freelance", "Investasi", "Lainnya")
    val expenseCategories = listOf(
        "Makanan", "Transportasi", "Utilitas", "Hiburan", "Kesehatan",
        "Pendidikan", "Belanja", "Kebutuhan Rumah", "Lainnya"
    )

    val categoryList = if (type == TransactionType.PEMASUKAN) incomeCategories else expenseCategories

    var expanded by remember { mutableStateOf(false) }
    val primaryBlue = Color(0xFF0066FF)

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

        // Dropdown kategori: menggunakan ExposedDropdownMenuBox
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

        SaviaOutlinedField(
            label = "Tanggal",
            placeholder = formattedDate,
            value = formattedDate,
            onValueChange = {},
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save button
        Button(
            onClick = {
                val rawAmount = amount.replace(",", "").toDoubleOrNull() ?: 0.0
                val finalAmount = if (type == TransactionType.PENGELUARAN) -abs(rawAmount) else abs(rawAmount)
                val tx = Transaction(
                    type = type, // Tipe sudah benar
                    description = description,
                    amount = finalAmount, // Gunakan nilai yang sudah disesuaikan
                    category = category.ifEmpty { if (type == TransactionType.PEMASUKAN) "Lainnya" else "Lainnya" },
                    date = date
                )
                onSave(tx)
                onDismiss()
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

/**
 * Small helper to show dropdown items using DropdownMenu (since ExposedDropdownMenuBox's
 * default ExposedDropdownMenu isn't directly usable in some versions).
 */
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
