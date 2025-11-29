package com.example.savia_finalproject.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
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
import androidx.navigation.NavHostController
import com.example.savia_finalproject.viewmodel.TransactionViewModel
import com.example.savia_finalproject.ui.components.BottomNavBar
import com.example.savia_finalproject.ui.components.TransactionBottomSheet // Import BottomSheet
import com.example.savia_finalproject.data.model.Transaction
import com.example.savia_finalproject.data.model.TransactionType
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
private val BlueGradientEnd = Color(0xFF4364F7)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TransactionScreen(viewModel: TransactionViewModel, navController: NavHostController) {
    val txs by viewModel.transactions.collectAsState()

    // --- 1. SETUP STATE UNTUK BOTTOM SHEET ---
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    // State untuk Filter
    var selectedType by remember { mutableStateOf<TransactionType?>(null) }
    var selectedMonth by remember { mutableStateOf<String?>(null) }

    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))

    // Data Grouping & Filtering
    val summaryMap = txs.sortedBy { it.date }.groupBy { monthFormat.format(it.date) }

    val filteredTransactions = txs.filter { tx ->
        val matchType = if (selectedType == null) true else tx.type == selectedType
        val matchMonth = if (selectedMonth == null) true else monthFormat.format(tx.date) == selectedMonth
        matchType && matchMonth
    }.sortedByDescending { it.date }

    val groupedForList = filteredTransactions.groupBy { monthFormat.format(it.date) }

    Scaffold(
        containerColor = BgGray,
        bottomBar = { BottomNavBar(navController = navController) },

        // --- 2. TAMBAHKAN FLOATING ACTION BUTTON (FAB) ---
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        showBottomSheet = true
                        sheetState.show()
                    }
                },
                containerColor = YellowAccent,
                contentColor = Color.Black,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Transaksi")
            }
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize()) {

            // Header Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(BluePrimary, BlueGradientEnd)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Riwayat Transaksi",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                if (txs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada data transaksi.", color = Color.Gray)
                    }
                } else {

                    // --- RINGKASAN BULANAN ---
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ringkasan Bulanan",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            if (selectedMonth != null) {
                                Text(
                                    text = "Reset Bulan",
                                    color = Color.Yellow,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { selectedMonth = null }
                                )
                            }
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp)
                        ) {
                            items(summaryMap.keys.toList()) { monthKey ->
                                val list = summaryMap[monthKey] ?: emptyList()
                                val income = list.filter { it.type == TransactionType.PEMASUKAN }.sumOf { it.amount }
                                val expense = list.filter { it.type == TransactionType.PENGELUARAN }.sumOf { it.amount }

                                val isSelected = selectedMonth == monthKey

                                MonthlyCardSelectable(
                                    monthName = monthKey,
                                    income = viewModel.formatCurrency(income),
                                    expense = viewModel.formatCurrency(expense),
                                    isSelected = isSelected,
                                    onClick = { selectedMonth = if (isSelected) null else monthKey }
                                )
                            }
                        }
                    }

                    // --- FILTER CHIPS ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChipBtn("Semua", selectedType == null) { selectedType = null }
                        FilterChipBtn("Pemasukan", selectedType == TransactionType.PEMASUKAN) { selectedType = TransactionType.PEMASUKAN }
                        FilterChipBtn("Pengeluaran", selectedType == TransactionType.PENGELUARAN) { selectedType = TransactionType.PENGELUARAN }
                    }

                    // --- LIST TRANSAKSI ---
                    if (filteredTransactions.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().padding(top = 40.dp), contentAlignment = Alignment.TopCenter) {
                            Text("Tidak ada transaksi sesuai filter.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 80.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            groupedForList.forEach { (month, transactionsInMonth) ->
                                stickyHeader {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(BgGray)
                                            .padding(horizontal = 24.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = month,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = BluePrimary
                                            )
                                        )
                                    }
                                }
                                items(transactionsInMonth) { tx ->
                                    TransactionItemSimple(tx)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- 3. IMPLEMENTASI BOTTOM SHEET ---
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    showBottomSheet = false
                }
            },
            sheetState = sheetState,
            containerColor = Color.White // Background putih agar sama dengan dashboard
        ) {
            TransactionBottomSheet(
                onDismiss = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showBottomSheet = false
                    }
                },
                onSave = { newTx ->
                    viewModel.addTransaction(newTx)
                }
            )
        }
    }
}

// ... (Komponen FilterChipBtn, MonthlyCardSelectable, TransactionItemSimple SAMA SEPERTI SEBELUMNYA) ...
// Sertakan kembali komponen di bawah ini agar file lengkap saat dicopy-paste

@Composable
fun FilterChipBtn(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) BluePrimary else Color.White,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
        modifier = Modifier.height(32.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = if (isSelected) Color.White else Color.Gray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun MonthlyCardSelectable(
    monthName: String,
    income: String,
    expense: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFFFFC107) else Color.Transparent
    val borderWidth = if (isSelected) 2.dp else 0.dp

    Card(
        modifier = Modifier
            .width(260.dp)
            .height(100.dp)
            .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, null, tint = BluePrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(monthName, fontWeight = FontWeight.Bold, color = BluePrimary, fontSize = 13.sp)
                }
                if (isSelected) {
                    Icon(Icons.Default.Check, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                }
            }
            Divider(color = Color.LightGray.copy(alpha = 0.3f))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowUpward, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(income, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF4CAF50))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowDownward, null, tint = Color(0xFFF44336), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(expense, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFF44336))
                }
            }
        }
    }
}

@Composable
fun TransactionItemSimple(transaction: Transaction) {
    val numberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    val isIncome = transaction.type == TransactionType.PEMASUKAN
    val amountColor = if (isIncome) Color(0xFF4CAF50) else Color(0xFFF44336)
    val icon = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID"))

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(amountColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = amountColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.description, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A1A1A))
                Text(dateFormat.format(transaction.date), fontSize = 11.sp, color = Color.Gray)
                if (transaction.category.isNotEmpty()) {
                    Text(transaction.category, fontSize = 11.sp, color = BluePrimary)
                }
            }
            Text(
                text = numberFormat.format(transaction.amount),
                color = amountColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}