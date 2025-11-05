package com.example.savia_finalproject.ui.authentication

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun signUpUser(email: String, password: String, context: Context, onSuccess: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                val userMap = hashMapOf(
                    "email" to email,
                    "createdAt" to System.currentTimeMillis()
                )
                db.collection("users").document(userId).set(userMap)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Sign up berhasil", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    }
            } else {
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}

fun loginUser(email: String, password: String, context: Context, onSuccess: () -> Unit) {
    val auth = FirebaseAuth.getInstance()

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Login berhasil", Toast.LENGTH_SHORT).show()
                onSuccess()
            } else {
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}
