package com.example.savia_finalproject.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.savia_finalproject.viewmodel.TransactionViewModel
import com.example.savia_finalproject.ui.components.TransactionItem
import java.text.SimpleDateFormat
import java.util.*
import com.example.savia_finalproject.data.model.TransactionType
import com.example.savia_finalproject.ui.components.BottomNavBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(viewModel: TransactionViewModel, navController: NavHostController) {
    val txs by viewModel.transactions.collectAsState()

    val dateFormat = SimpleDateFormat("MM-yyyy", Locale.getDefault())
    val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())

    // Rekap per bulan & tahun
    val monthlySummary = txs.groupBy { dateFormat.format(it.date) }
    val yearlySummary = txs.groupBy { yearFormat.format(it.date) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Semua Transaksi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (txs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada transaksi")
                }
            } else {
                Text(
                    "Rekap Bulanan",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                monthlySummary.forEach { (month, list) ->
                    val income = list.filter { it.type == TransactionType.PEMASUKAN }.sumOf { it.amount }
                    val expense = list.filter { it.type == TransactionType.PENGELUARAN }.sumOf { it.amount }
                    Text("• $month: +${viewModel.formatCurrency(income)} / -${viewModel.formatCurrency(expense)}")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Rekap Tahunan",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                yearlySummary.forEach { (year, list) ->
                    val income = list.filter { it.type == TransactionType.PEMASUKAN }.sumOf { it.amount }
                    val expense = list.filter { it.type == TransactionType.PENGELUARAN }.sumOf { it.amount }
                    Text("• $year: +${viewModel.formatCurrency(income)} / -${viewModel.formatCurrency(expense)}")
                }

                Spacer(modifier = Modifier.height(24.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Daftar Transaksi Lengkap",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(txs.reversed()) { tx ->
                        TransactionItem(tx)
                    }
                }
            }
        }
    }
}
