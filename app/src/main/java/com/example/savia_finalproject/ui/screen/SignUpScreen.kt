package com.example.savia_finalproject.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.savia_finalproject.R
import com.example.savia_finalproject.ui.components.SaviaOutlinedField
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun SignUpScreen(
    navController: NavController,
    auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween 
    ) {

        // Bagian atas - logo + judul
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_aplikasi),
                contentDescription = "Logo Savia",
                modifier = Modifier
                    .size(200.dp) // Ukuran logo disesuaikan agar lebih proporsional
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Text(
                text = "Solusi Keuangan Pintar",
                fontSize = 30.sp, // Ukuran font disesuaikan agar seimbang
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))


            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Daftar Akun",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = androidx.compose.ui.graphics.Color.LightGray
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = androidx.compose.ui.graphics.Color.LightGray
                )
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Konfirmasi Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = androidx.compose.ui.graphics.Color.LightGray
                )
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (password == confirmPassword) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val userId = auth.currentUser?.uid

                                    val db = FirebaseFirestore.getInstance()
                                    val user = hashMapOf(
                                        "email" to email,
                                        "createdAt" to System.currentTimeMillis(),
                                        "role" to "user",
                                        "balance" to 0
                                    )

                                    if (userId != null) {
                                        db.collection("users").document(userId).set(user)
                                            .addOnSuccessListener {
                                                message = "Pendaftaran berhasil!"
                                                navController.navigate("login")
                                            }
                                            .addOnFailureListener { e ->
                                                message = "Gagal menyimpan data: ${e.message}"
                                            }
                                    }
                                } else {
                                    message = task.exception?.message ?: "Gagal mendaftar"
                                }
                            }
                    } else {
                        message = "Password tidak cocok!"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = YellowAccent,
                    contentColor = TextBlack
                )
            ) {
                Text("Daftar")
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.padding(bottom = 75.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sudah punya akun?", color = androidx.compose.ui.graphics.Color.Gray)
                TextButton(onClick = { navController.navigate("login") }) {
                    Text("Login di sini", color = BluePrimary, fontWeight = FontWeight.Bold)
                }
            }

            if (message.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(message)
            }
        }

        // Bagian bawah - teks tambahan
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "SAVIA S NYA SUKSES",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

