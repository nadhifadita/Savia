package com.example.savia_finalproject.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.savia_finalproject.data.model.Transaction
import com.example.savia_finalproject.data.model.TransactionType
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionBottomSheet(
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit
) {
    var type by remember { mutableStateOf(TransactionType.PENGELUARAN) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var amount by remember { mutableStateOf(TextFieldValue("")) }
    var category by remember { mutableStateOf(TextFieldValue("")) }
    val date = remember { Date() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Tambah Transaksi", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick = { type = TransactionType.PEMASUKAN },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (type == TransactionType.PEMASUKAN) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) { Text("Pemasukan") }

                Button(
                    onClick = { type = TransactionType.PENGELUARAN },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (type == TransactionType.PENGELUARAN) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) { Text("Pengeluaran") }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi") },
                placeholder = { Text("Contoh: Gaji, Makan siang") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Jumlah (Rp)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Kategori") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val amt = amount.text.toDoubleOrNull() ?: 0.0
                    onSave(
                        Transaction(
                            type = type,
                            description = description.text,
                            amount = amt,
                            category = category.text,
                            date = date
                        )
                    )
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Transaksi")
            }
        }
    }
}
