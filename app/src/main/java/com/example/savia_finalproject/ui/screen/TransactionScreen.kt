// File: com/example/savia_finalproject/ui/screen/TransactionScreen.kt

package com.example.savia_finalproject.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savia_finalproject.ui.components.TransactionItem
import com.example.savia_finalproject.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    // Anda mungkin perlu menambahkan navController untuk navigasi
    // navController: NavHostController,
    transactionViewModel: TransactionViewModel = viewModel()
) {
    // Kumpulkan state dari ViewModel
    val uiState by transactionViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Transaksi", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF4F6F9) // Warna latar belakang yang sedikit abu-abu
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                // Tampilkan loading indicator saat data dimuat
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }

                // Tampilkan pesan error jika ada
                uiState.error != null -> {
                    Text(text = "Error: ${uiState.error}")
                }

                // Tampilkan daftar transaksi jika tidak kosong
                uiState.transactions.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Gunakan items untuk menampilkan daftar
                        items(uiState.transactions, key = { it.id }) { transaction ->
                            TransactionItem(transaction = transaction)
                        }
                    }
                }

                // Tampilkan pesan jika tidak ada transaksi
                else -> {
                    Text("Belum ada transaksi.")
                }
            }
        }
    }
}

