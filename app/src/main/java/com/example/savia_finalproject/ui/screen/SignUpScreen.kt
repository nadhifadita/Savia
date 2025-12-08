package com.example.savia_finalproject.ui.screen

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.savia_finalproject.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

private val BlueGradientEnd = Color(0xFF4364F7)

@Composable
fun SignUpScreen(
    navController: NavController,
    auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current

    val token = stringResource(id = R.string.default_web_client_id)

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(token)
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, gso)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken!!

                val credential = GoogleAuthProvider.getCredential(idToken, null)

                auth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            val user = auth.currentUser
                            val uid = user?.uid
                            val db = FirebaseFirestore.getInstance()

                            if (uid != null) {
                                val docRef = db.collection("users").document(uid)
                                docRef.get().addOnSuccessListener { document ->
                                    if (!document.exists()) {
                                        val newUserMap = hashMapOf(
                                            "email" to (user.email ?: ""),
                                            "name" to (user.displayName ?: "User"),
                                            "createdAt" to System.currentTimeMillis(),
                                            "role" to "user",
                                            "balance" to 0
                                        )
                                        docRef.set(newUserMap)
                                    }
                                    Toast.makeText(context, "Login Google Berhasil", Toast.LENGTH_SHORT).show()
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        } else {
                            message = "Gagal Login Firebase: ${authTask.exception?.message}"
                        }
                    }
            } catch (e: ApiException) {
                message = "Gagal Login Google: ${e.message}"
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier.fillMaxWidth().height(260.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(BluePrimary, BlueGradientEnd)
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 75.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Daftar Akun",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Mulai perjalanan keuanganmu",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(110.dp),
                    shape = CircleShape,
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_aplikasi),
                            contentDescription = "Logo Savia",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // FORM INPUT
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Konfirmasi Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (password == confirmPassword && email.isNotEmpty()) {
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
                                                        message = "Gagal simpan data: ${e.message}"
                                                    }
                                            }
                                        } else {
                                            message = task.exception?.message ?: "Gagal mendaftar"
                                        }
                                    }
                            } else {
                                message = "Cek kembali data Anda"
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = YellowAccent,
                            contentColor = TextBlack
                        ),
                        elevation = ButtonDefaults.buttonElevation(6.dp)
                    ) {
                        Text("DAFTAR SEKARANG", fontWeight = FontWeight.Bold)
                    }

                    if (message.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        HorizontalDivider(modifier = Modifier.weight(1f))
                        Text(" ATAU ", color = Color.Gray, fontSize = 12.sp)
                        HorizontalDivider(modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            launcher.launch(googleSignInClient.signInIntent)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Image(painter = painterResource(id = R.drawable.logo_google), contentDescription = "Google Logo", modifier = Modifier.size(20.dp))
                        Text("Daftar dengan Google", color = Color.Black, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Sudah punya akun?", color = Color.Gray)
                TextButton(onClick = { navController.navigate("login") }) {
                    Text("Login di sini", color = BluePrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun stringResource(id: Int): String {
    return LocalContext.current.getString(id)
}