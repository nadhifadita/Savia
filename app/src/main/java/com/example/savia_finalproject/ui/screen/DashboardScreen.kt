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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.savia_finalproject.viewmodel.TransactionViewModel
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import kotlin.math.abs
import com.example.savia_finalproject.ui.components.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

val BluePrimary = Color(0xFF0052D4)
val YellowAccent = Color(0xFFFFC107)
val TextBlack = Color(0xFF1A1A1A)
val BgGray = Color(0xFFF5F7FA)

// Model Data untuk Notifikasi
data class AppNotification(
    val title: String,
    val message: String,
    val type: String // "ALERT", "INFO"
)

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

    var showNotificationMenu by remember { mutableStateOf(false) }

    val weeklyChartDataState by viewModel.weeklyChartData.collectAsState()
    val (weeklyEntries, weeklyLabels) = weeklyChartDataState
    val monthlyChartDataState by viewModel.monthlyChartData.collectAsState()
    val (monthlyEntries, monthlyLabels) = monthlyChartDataState
    val txs by viewModel.transactions.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()

    val notifications = remember(balance, totalExpense, txs) {
        val list = mutableListOf<AppNotification>()

        if (balance < 50000 && balance > 0) {
            list.add(AppNotification("Saldo Menipis!", "Sisa saldo Anda di bawah Rp 50.000", "ALERT"))
        } else if (balance <= 0) {
            list.add(AppNotification("Saldo Kosong", "Segera catat pemasukan baru.", "ALERT"))
        }

        if (abs(totalExpense) > 1000000) {
            list.add(AppNotification("Pengeluaran Tinggi", "Anda sudah menghabiskan > 1 Juta minggu ini.", "INFO"))
        }

        if (list.isEmpty()) {
            list.add(AppNotification("Selamat Datang", "Belum ada peringatan keuangan penting.", "INFO"))
        }

        list
    }

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
        containerColor = Color.Transparent,
        bottomBar = { BottomNavBar(navController = navController) }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgGray)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(BluePrimary, Color(0xFF4364F7))
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 48.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp),
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

                            Box {
                                IconButton(
                                    onClick = { showNotificationMenu = true },
                                    modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                                ) {
                                    Box {
                                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                                        if (notifications.any { it.type == "ALERT" }) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .background(Color.Red, CircleShape)
                                                    .align(Alignment.TopEnd)
                                                    .offset(x = 2.dp, y = (-2).dp)
                                            )
                                        }
                                    }
                                }

                                DropdownMenu(
                                    expanded = showNotificationMenu,
                                    onDismissRequest = { showNotificationMenu = false },
                                    modifier = Modifier
                                        .width(250.dp)
                                        .background(Color.White, RoundedCornerShape(12.dp))
                                ) {
                                    notifications.forEach { notif ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                    Text(notif.message, fontSize = 12.sp, color = Color.Gray)
                                                }
                                            },
                                            leadingIcon = {
                                                if (notif.type == "ALERT") {
                                                    Icon(Icons.Default.Warning, null, tint = Color.Red)
                                                } else {
                                                    Icon(Icons.Default.Info, null, tint = BluePrimary)
                                                }
                                            },
                                            onClick = { showNotificationMenu = false }
                                        )
                                        Divider(color = Color.LightGray.copy(alpha = 0.2f))
                                    }
                                }
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Total Saldo Anda", color = Color.Gray, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))

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

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(Color(0xFFE8F5E9), CircleShape),
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

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(Color(0xFFFFEBEE), CircleShape),
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
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
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
                            containerColor = YellowAccent,
                            contentColor = Color.Black
                        ),
                        elevation = ButtonDefaults.buttonElevation(6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tambah Transaksi Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(30.dp))

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

                    if (txs.isEmpty()) {
                        Text("Belum ada transaksi", color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))
                    } else {
                        val recentTxs = txs.reversed().take(5)
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            recentTxs.forEach { tx ->
                                TransactionItem(tx)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
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