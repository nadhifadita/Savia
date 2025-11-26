package com.example.savia_finalproject.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.savia_finalproject.viewmodel.TransactionViewModel
import androidx.compose.runtime.collectAsState
import com.example.savia_finalproject.data.model.Transaction
import androidx.navigation.NavHostController
import kotlin.math.abs
import com.example.savia_finalproject.ui.components.* // Pastikan import komponen lain
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

val BluePrimary = Color(0xFF0052D4)
val BlueDark = Color(0xFF20BDFF)
val YellowAccent = Color(0xFFFFC107)
val TextBlack = Color(0xFF1A1A1A)
val BgGray = Color(0xFFF5F7FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: TransactionViewModel, navController: NavHostController) {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val user = auth.currentUser
    var balance by remember { mutableStateOf(0.0) }
    var userName by remember { mutableStateOf("Sobat Savia") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Data Chart & Transaksi
    val weeklyChartDataState by viewModel.weeklyChartData.collectAsState()
    val (weeklyEntries, weeklyLabels) = weeklyChartDataState

    val monthlyChartDataState by viewModel.monthlyChartData.collectAsState()
    val (monthlyEntries, monthlyLabels) = monthlyChartDataState

    val txs by viewModel.transactions.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()

    // Fetch Data User
    LaunchedEffect(user) {
        user?.uid?.let { uid ->
            db.collection("users").document(uid)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null && snapshot.exists()) {
                        balance = snapshot.getDouble("balance") ?: 0.0
                        userName = snapshot.getString("name") ?: user.email?.split("@")?.get(0) ?: "User"
                    }
                }
        }
    }

    Scaffold(
        containerColor = BgGray, // Background Abu-abu muda
        bottomBar = { BottomNavBar(navController = navController) }
    ) { innerPadding ->

        // GUNAKAN BOX UNTUK LAYER BACKGROUND BIRU
        Box(modifier = Modifier.fillMaxSize()) {

            // 1. Header Lengkung Biru
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp) // Tinggi header biru
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(BluePrimary, Color(0xFF4364F7))
                        )
                    )
            )

            // 2. Konten Utama (Scrollable)
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {

                // --- Top Bar (Nama & Notifikasi) ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Selamat Datang,",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = userName.replaceFirstChar { it.uppercase() },
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    // Icon Notifikasi (Hiasan)
                    IconButton(
                        onClick = { /* TODO */ },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                    }
                }

                // --- Modern Balance Card (Putih di atas Biru) ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(10.dp), // Shadow agar "pop"
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Total Saldo Anda", color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Saldo Besar Biru
                        Text(
                            text = viewModel.formatCurrency(balance),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )

                        Divider(
                            modifier = Modifier.padding(vertical = 20.dp),
                            color = Color.LightGray.copy(alpha = 0.3f)
                        )

                        // Row Income/Expense
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Item Pemasukan
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFFE8F5E9), CircleShape), // Hijau muda soft
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.ArrowUpward, null, tint = Color(0xFF4CAF50))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Pemasukan", fontSize = 12.sp, color = Color.Gray)
                                    Text(
                                        viewModel.formatCurrency(totalIncome),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            // Item Pengeluaran
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFFFFEBEE), CircleShape), // Merah muda soft
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.ArrowDownward, null, tint = Color(0xFFF44336))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Pengeluaran", fontSize = 12.sp, color = Color.Gray)
                                    Text(
                                        viewModel.formatCurrency(abs(totalExpense)),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                FinancialChart(
                    weeklyEntries = weeklyEntries,
                    weeklyLabels = weeklyLabels,
                    monthlyEntries = monthlyEntries,
                    monthlyLabels = monthlyLabels
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            showBottomSheet = true
                            sheetState.show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YellowAccent, // Kuning
                        contentColor = Color.Black     // Teks Hitam agar terbaca
                    ),
                    elevation = ButtonDefaults.buttonElevation(6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tambah Transaksi Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(30.dp))

                // --- List Transaksi (Header) ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Riwayat Terakhir",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                    TextButton(onClick = { navController.navigate("transaksi") }) {
                        Text("Lihat Semua", color = BluePrimary)
                    }
                }

                // List Item
                if (txs.isEmpty()) {
                    Text("Belum ada transaksi", color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))
                } else {
                    val recentTxs = txs.reversed().take(5)
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        recentTxs.forEach { tx ->
                            // Transaction Item standar
                            TransactionItem(tx)
                        }
                    }
                }

                // Spacing bawah agar tidak tertutup nav bar
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Bottom Sheet Logic (Sama seperti sebelumnya)
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                    showBottomSheet = false
                }
            },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            TransactionBottomSheet(
                onDismiss = {
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        showBottomSheet = false
                    }
                },
                onSave = { newTx -> viewModel.addTransaction(newTx) }
            )
        }
    }
}