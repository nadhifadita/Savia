package com.example.savia_finalproject.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.savia_finalproject.data.model.Goal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalBottomSheet(
    onDismiss: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var savedAmount by remember { mutableStateOf("") }

    val primaryBlue = Color(0xFF0066FF)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Tambah Goals Baru", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Input Fields
        SaviaOutlinedField(
            label = "Nama Goal",
            placeholder = "Contoh: Beli Laptop",
            value = title,
            onValueChange = { title = it }
        )

        SaviaOutlinedField(
            label = "Target (Rp)",
            placeholder = "0",
            value = targetAmount,
            onValueChange = { targetAmount = it }
        )

        SaviaOutlinedField(
            label = "Dana Terkumpul (opsional)",
            placeholder = "0",
            value = savedAmount,
            onValueChange = { savedAmount = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save button
        Button(
            onClick = {
                val auth = FirebaseAuth.getInstance()
                val db = FirebaseFirestore.getInstance()
                val userId = auth.currentUser?.uid

                if (userId != null) {
                    val userGoalsRef = db.collection("users")
                        .document(userId)
                        .collection("goals")

                    val goal = Goal(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        targetAmount = targetAmount.replace(",", "").toLongOrNull() ?: 0,
                        savedAmount = savedAmount.replace(",", "").toLongOrNull() ?: 0,
                        isCompleted = false,
                        createdAt = System.currentTimeMillis()
                    )

                    userGoalsRef.document(goal.id)
                        .set(goal)
                        .addOnSuccessListener {
                            onSaveSuccess()
                            onDismiss()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Gagal menyimpan goal", e)
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
        ) {
            Text("Simpan Goals", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
