package com.example.savia_finalproject.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.savia_finalproject.data.api.NewsArticle
import com.example.savia_finalproject.data.api.ProductResponse
import com.example.savia_finalproject.ui.components.BottomNavBar
import com.example.savia_finalproject.viewmodel.EducationViewModel
import java.text.NumberFormat
import java.util.Locale

private val BlueGradientEnd = Color(0xFF4364F7)

@Composable
fun EducationScreen(
    navController: NavController,
    viewModel: EducationViewModel = viewModel()
) {
    var showLoanCalculator by remember { mutableStateOf(false) }
    var showSavingsCalculator by remember { mutableStateOf(false) }

    val newsList by viewModel.newsList.collectAsState()
    val productList by viewModel.productList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val context = LocalContext.current

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        // Set container color transparan agar background header bisa full
        containerColor = Color.Transparent
    ) { padding ->

        // Background Abu untuk seluruh layar di belakang
        Box(modifier = Modifier.fillMaxSize().background(BgGray)) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                        .background(
                            brush = Brush.verticalGradient(colors = listOf(BluePrimary, BlueGradientEnd))
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 24.dp, end = 24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Pusat Literasi", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Kelola masa depan keuangan usahamu dengan data pasar terkini.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp),
                    contentPadding = PaddingValues(
                        bottom = padding.calculateBottomPadding() + 24.dp
                    )
                ) {

                    item {
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.cardElevation(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text("Alat Bantu Hitung", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        CalculatorItem(
                                            title = "Kalkulator\nCicilan",
                                            icon = Icons.Default.Calculate,
                                            color = BluePrimary,
                                            onClick = { showLoanCalculator = true },
                                            modifier = Modifier.weight(1f)
                                        )
                                        CalculatorItem(
                                            title = "Target\nMenabung",
                                            icon = Icons.Default.Savings,
                                            color = Color(0xFF4CAF50),
                                            onClick = { showSavingsCalculator = true },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp)) {
                            Text("Peluang Investasi Terkini", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A1A))
                            Text("Data diambil secara real-time", fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (productList.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = BluePrimary)
                            }
                        } else {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(productList) { product ->
                                    ProductCardReal(product) {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.url))
                                        context.startActivity(intent)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Kabar Bisnis", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A1A))
                                IconButton(onClick = { viewModel.fetchNews() }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = BluePrimary)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    items(newsList) { article ->
                        Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)) {
                            NewsItemCard(article) { url ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showLoanCalculator) LoanCalculatorDialog { showLoanCalculator = false }
    if (showSavingsCalculator) SavingsTargetDialog { showSavingsCalculator = false }
}

@Composable
fun CalculatorItem(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .background(Color(0xFFF5F7FA), RoundedCornerShape(12.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.Center, lineHeight = 14.sp)
    }
}

@Composable
fun ProductCardReal(product: ProductResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(190.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(YellowAccent.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = product.getIcon(),
                    contentDescription = null,
                    tint = Color(0xFFF57F17),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 2,
                minLines = 2,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                overflow = TextOverflow.Ellipsis,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = product.rate,
                fontSize = 12.sp,
                color = BluePrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Risiko: ${product.risk}",
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SavingsTargetDialog(onDismiss: () -> Unit) {
    var targetAmount by remember { mutableStateOf("") }
    var durationMonths by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Target Menabung", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Hitung berapa yang harus disisihkan per bulan untuk mencapai tujuanmu.", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = { if (it.all { char -> char.isDigit() }) targetAmount = it },
                    label = { Text("Target Dana (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = durationMonths,
                    onValueChange = { if (it.all { char -> char.isDigit() }) durationMonths = it },
                    label = { Text("Waktu (Bulan)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val target = targetAmount.toDoubleOrNull() ?: 0.0
                        val months = durationMonths.toDoubleOrNull() ?: 0.0

                        if (target > 0 && months > 0) {
                            val monthlySaving = target / months
                            val formatRp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                            resultText = "Sisihkan per bulan:\n${formatRp.format(monthlySaving)}"
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Hitung Target", fontWeight = FontWeight.Bold)
                }

                if (resultText != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(10.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = resultText!!,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF2E7D32),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NewsItemCard(article: NewsArticle, onClick: (String) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(article.url) }
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(80.dp)
            ) {
                AsyncImage(
                    model = article.urlToImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.source?.name ?: "Berita",
                    fontSize = 10.sp,
                    color = BluePrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = article.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.description ?: "Baca selengkapnya...",
                    maxLines = 2,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun LoanCalculatorDialog(onDismiss: () -> Unit) {
    var loanAmount by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var durationMonths by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kalkulator Cicilan", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = loanAmount,
                    onValueChange = { if (it.all { char -> char.isDigit() }) loanAmount = it },
                    label = { Text("Jumlah Pinjaman (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = interestRate,
                        onValueChange = { interestRate = it },
                        label = { Text("Bunga (%/Thn)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = durationMonths,
                        onValueChange = { if (it.all { char -> char.isDigit() }) durationMonths = it },
                        label = { Text("Tenor (Bln)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        val p = loanAmount.toDoubleOrNull() ?: 0.0
                        val r = (interestRate.toDoubleOrNull() ?: 0.0) / 100.0
                        val t = durationMonths.toDoubleOrNull() ?: 0.0
                        if (p > 0 && t > 0) {
                            val totalInterest = p * r * (t / 12)
                            val totalPay = p + totalInterest
                            val monthly = totalPay / t
                            val formatRp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                            resultText = "Cicilan per bulan:\n${formatRp.format(monthly)}"
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = YellowAccent, contentColor = Color.Black)
                ) {
                    Text("Hitung Sekarang", fontWeight = FontWeight.Bold)
                }
                if (resultText != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().background(Color(0xFFE3F2FD), RoundedCornerShape(10.dp)).padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = resultText!!, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BluePrimary, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}