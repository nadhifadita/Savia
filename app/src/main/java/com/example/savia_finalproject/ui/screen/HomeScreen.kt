package com.example.savia_finalproject.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.savia_finalproject.data.model.Transaction
import com.example.savia_finalproject.ui.components.TransactionBottomSheet
import com.example.savia_finalproject.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: TransactionViewModel) {
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showSheet = true }
            ) {
                Text("Tambah Transaksi")
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            // Konten utama (saldo, daftar transaksi, dll)
        }

        if (showSheet) {
            TransactionBottomSheet(
                onDismiss = { showSheet = false },
                onSave = { viewModel.addTransaction(it) }
            )
        }
    }
}
