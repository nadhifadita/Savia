package com.example.savia_finalproject.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.savia_finalproject.data.model.Goal
import com.example.savia_finalproject.viewmodel.GoalsViewModel
import com.example.savia_finalproject.viewmodel.viewmodel.GoalsViewModelFactory
import com.example.savia_finalproject.data.repository.GoalsRepository
import com.example.savia_finalproject.ui.components.BottomNavBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

private val BlueGradientEnd = Color(0xFF4364F7)

@Composable
fun GoalScreen(navController: NavHostController) {

    val viewModel: GoalsViewModel = viewModel(
        factory = GoalsViewModelFactory(
            GoalsRepository(
                FirebaseAuth.getInstance(),
                FirebaseFirestore.getInstance()
            )
        )
    )

    val goals by viewModel.goals.collectAsState()
    val balance by viewModel.balance.collectAsState()

    val formatRp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Tambah Goals Baru */ },
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
            // Header
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

                    Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))) {
                        Text(
                            "Total Tabungan: ${formatRp.format(balance)}",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // List Goals dari Firestore
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(goals) { goal ->
                    GoalCard(
                        goal = goal,
                        balance = balance,
                        onConvert = { viewModel.convertGoal(goal) }
                    )
                }
            }
        }
    }
}

@Composable
fun GoalCard(goal: com.example.savia_finalproject.data.model.Goal, balance: Long, onConvert: () -> Unit) {

    val progress = (balance.toFloat() / goal.targetAmount).coerceIn(0f, 1f)
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
                Text(text = goal.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                if (goal.isCompleted) {
                    Text("Tercapai! 🎉", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                } else {
                    Text("${(progress * 100).toInt()}%", color = BluePrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = if (goal.isCompleted) Color(0xFF4CAF50) else BluePrimary,
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatRp.format(balance), color = Color.Gray, fontSize = 12.sp)
                Text(formatRp.format(goal.targetAmount), color = Color.Gray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Jika balance mencukupi dan goal belum completed
            if (!goal.isCompleted && balance >= goal.targetAmount) {
                Button(
                    onClick = onConvert,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Gunakan Dana")
                }
            }
        }
    }
}