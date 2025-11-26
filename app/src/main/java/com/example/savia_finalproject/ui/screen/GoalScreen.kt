package com.example.savia_finalproject.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Flag
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
import androidx.navigation.NavController
import com.example.savia_finalproject.ui.components.BottomNavBar
import java.text.NumberFormat
import java.util.Locale

private val BlueGradientEnd = Color(0xFF4364F7)

data class Goal(val name: String, val current: Double, val target: Double)

val dummyGoals = listOf(
    Goal("Beli Laptop Baru", 2500000.0, 10000000.0),
    Goal("Dana Darurat", 5000000.0, 5000000.0),
    Goal("Renovasi Toko", 1500000.0, 20000000.0)
)

@Composable
fun GoalScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Tambah Goal Baru */ },
                containerColor = YellowAccent
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Target", tint = Color.Black)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                    .background(
                        brush = Brush.verticalGradient(colors = listOf(BluePrimary, BlueGradientEnd))
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Target Keuangan", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Wujudkan impian usahamu!", color = Color.White.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(16.dp))
                    // Summary Singkat
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))) {
                        Text(
                            "Total Tabungan: Rp 9.000.000",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // List Goals
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(dummyGoals) { goal ->
                    GoalCard(goal)
                }
            }
        }
    }
}

@Composable
fun GoalCard(goal: Goal) {
    val progress = (goal.current / goal.target).toFloat()
    val formatRp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = goal.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (progress >= 1.0f) {
                    Text("Tercapai! 🎉", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                } else {
                    Text("${(progress * 100).toInt()}%", color = BluePrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = if (progress >= 1.0f) Color(0xFF4CAF50) else BluePrimary,
                trackColor = Color.LightGray.copy(alpha = 0.3f),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = formatRp.format(goal.current), fontSize = 12.sp, color = Color.Gray)
                Text(text = formatRp.format(goal.target), fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}