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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.savia_finalproject.data.model.Goals
import com.example.savia_finalproject.data.repository.GoalsRepository
import com.example.savia_finalproject.ui.components.BottomNavBar
import com.example.savia_finalproject.ui.components.GoalsBottomSheet
import com.example.savia_finalproject.viewmodel.GoalsViewModel
import com.example.savia_finalproject.viewmodel.viewmodel.GoalsViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

private val BlueGradientEnd = Color(0xFF4364F7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val viewModel: GoalsViewModel = viewModel(
        factory = GoalsViewModelFactory(
            GoalsRepository(
                FirebaseAuth.getInstance(),
                FirebaseFirestore.getInstance()
            )
        )
    )

    val goals by viewModel.goals.collectAsState()
    var balance by remember { mutableStateOf(0.0) }

    val formatRp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val user = auth.currentUser

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

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showBottomSheet = true
                },
                containerColor = YellowAccent,
                contentColor = Color.Black,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Goal")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgGray)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = padding.calculateBottomPadding())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                        .background(
                            brush = Brush.verticalGradient(colors = listOf(BluePrimary, BlueGradientEnd))
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 60.dp, start = 24.dp, end = 24.dp)
                    ) {
                        Text(
                            text = "Target Keuangan",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Wujudkan impian usahamu!",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = "Total Tabungan Tersedia",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = formatRp.format(balance),
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp, start = 20.dp, end = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items = goals, key = { it.id }) { goal ->
                        GoalCard(goal = goal, balance = balance.toLong()) { selectedGoal ->
                            viewModel.convertGoal(selectedGoal)
                        }
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                GoalsBottomSheet(
                    onDismiss = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    },
                    onSave = { newGoal ->
                        viewModel.addGoal(
                            newGoal.title,
                            newGoal.targetAmount
                        )
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun GoalCard(goal: Goals, balance: Long, onConvert: (Goals) -> Unit) {
    val progress = if (goal.targetAmount > 0) (balance.toFloat() / goal.targetAmount).coerceIn(0f, 1f) else 0f
    val formatRp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    val isCompleted = goal.isCompleted
    val progressColor = if (isCompleted) Color(0xFF4CAF50) else BluePrimary

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp), // Shadow lebih halus
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if(isCompleted) "Selesai" else "Dalam Proses",
                        fontSize = 12.sp,
                        color = if(isCompleted) Color(0xFF4CAF50) else Color.Gray
                    )
                }

                if (goal.isCompleted) {
                    Text("Tercapai! \uD83C\uDF89", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                } else {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        color = BluePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = progressColor,
                trackColor = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Terkumpul", fontSize = 10.sp, color = Color.Gray)
                    Text(formatRp.format(balance), color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Target", fontSize = 10.sp, color = Color.Gray)
                    Text(formatRp.format(goal.targetAmount), color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            if (!goal.isCompleted && balance >= goal.targetAmount) {
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onConvert(goal) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("Gunakan Dana", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}