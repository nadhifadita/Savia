package com.example.savia_finalproject.ui.screen

import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.savia_finalproject.R
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
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo aplikasi",
                modifier = Modifier.size(300.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))


            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Daftar Akun",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
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
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Konfirmasi Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Daftar")
            }

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text("Sudah punya akun? Login di sini")
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

