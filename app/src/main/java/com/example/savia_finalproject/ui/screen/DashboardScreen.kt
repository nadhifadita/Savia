package com.example.savia_finalproject.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.savia_finalproject.ui.components.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.savia_finalproject.viewmodel.TransactionViewModel
import androidx.compose.runtime.collectAsState
import com.example.savia_finalproject.data.model.Transaction
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlin.math.abs
import com.example.savia_finalproject.ui.components.WeeklyChart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun DashboardScreen(viewModel: TransactionViewModel, navController: NavHostController) {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val user = auth.currentUser
    var balance by remember { mutableStateOf(0.0) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val weeklyChartDataState by viewModel.weeklyChartData.collectAsState()
    val (weeklyEntries, weeklyLabels) = weeklyChartDataState

    // observe transactions
    val txs by viewModel.transactions.collectAsState()
    val totalBalance by viewModel.totalBalance.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()


    LaunchedEffect(user) {
        user?.uid?.let { uid ->
            db.collection("users").document(uid)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null && snapshot.exists()) {
                        balance = snapshot.getDouble("balance") ?: 0.0
                    }
                }
        }
    }

    // Format balance
    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "SAVIA",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Text(text = "Kelola keuangan Anda dengan cerdas", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))

            BalanceCard(
                balance = viewModel.formatCurrency(balance),
                income = viewModel.formatCurrency(totalIncome),
                // Gunakan abs() untuk menghilangkan tanda negatif saat ditampilkan
                expense = viewModel.formatCurrency(abs(totalExpense))
            )
            Spacer(modifier = Modifier.height(24.dp))

            WeeklyChart(entries = weeklyEntries, labels = weeklyLabels)
            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Aksi Cepat", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        showBottomSheet = true
                        sheetState.show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tambah Transaksi")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Transaksi Terakhir", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            if (txs.isEmpty()) {
                Text(
                    text = "Belum ada transaksi",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                val recentTxs = txs.reversed().take(10)
                Column(modifier = Modifier.fillMaxWidth()) {
                    recentTxs.forEach { tx ->
                        TransactionItem(tx)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { navController.navigate("transaksi") },
                        modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)
                    ) {
                        Text("Lihat Semua Transaksi", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                    showBottomSheet = false
                }
            },
            sheetState = sheetState,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            // content composable
            TransactionBottomSheet(
                onDismiss = {
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        showBottomSheet = false
                    }
                },
                onSave = { newTx: Transaction ->
                    viewModel.addTransaction(newTx)
                }
            )
        }
    }
}
