package com.example.savia_finalproject.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.savia_finalproject.ui.components.BottomNavBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

private val BlueGradientEnd = Color(0xFF4364F7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val user = auth.currentUser

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("********") } // Mock password
    var createdAt by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Fetch Data
    LaunchedEffect(user) {
        user?.uid?.let { uid ->
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        email = document.getString("email") ?: ""
                        val timestamp = document.getLong("createdAt") ?: 0L
                        if (timestamp != 0L) {
                            val date = Date(timestamp)
                            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            createdAt = formatter.format(date)
                        } else {
                            createdAt = "-"
                        }
                    }
                }
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) },
        containerColor = BgGray // Background abu muda
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. HEADER AREA (Overlap Style)
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier.fillMaxWidth().height(220.dp) // Tinggi area header total
                ) {
                    // Background Biru Lengkung
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 50.dp) // Beri ruang bawah agar foto overlap
                            .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(BluePrimary, BlueGradientEnd)
                                )
                            )
                    ) {
                        // Judul Halaman di pojok kiri atas
                        Text(
                            text = "Profil Pengguna",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 40.dp, start = 24.dp)
                        )
                    }

                    // Foto Profil (Lingkaran) yang overlap
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .shadow(8.dp, CircleShape)
                            .background(Color.White, CircleShape)
                            .border(4.dp, Color.White, CircleShape), // Border putih tebal
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(60.dp),
                            tint = BluePrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 2. KONTEN (CARD)
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {

                    Text(
                        text = "Detail Akun",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1A1A1A),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Email
                            ProfileField(label = "Email", value = email, icon = Icons.Default.Person)

                            Divider(color = Color.LightGray.copy(alpha = 0.3f))

                            // Password
                            // Custom field untuk password visibility
                            Column {
                                Text("Password", fontSize = 12.sp, color = Color.Gray)
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = {},
                                    readOnly = true,
                                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    trailingIcon = {
                                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                            Icon(
                                                imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                                contentDescription = null,
                                                tint = BluePrimary
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedBorderColor = Color.Transparent
                                    ),
                                    textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }

                            Divider(color = Color.LightGray.copy(alpha = 0.3f))

                            // Created At
                            ProfileField(label = "Bergabung Sejak", value = createdAt, icon = null)
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // 3. TOMBOL AKSI

                    // Tombol Kembali (Outlined Blue)
                    OutlinedButton(
                        onClick = {
                            navController.navigate("dashboard") {
                                popUpTo("profile") { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BluePrimary)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = BluePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kembali ke Dashboard", color = BluePrimary)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tombol Logout (Merah)
                    Button(
                        onClick = {
                            auth.signOut()
                            navController.navigate("login") {
                                popUpTo("profile") { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)), // Merah sangat muda
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text("Keluar Aplikasi", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

// Komponen Kecil untuk Field agar rapi
@Composable
fun ProfileField(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector?) {
    Column {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        }
    }
}