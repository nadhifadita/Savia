package com.example.savia_finalproject.data.repository

import android.util.Log
import com.example.savia_finalproject.data.model.Goal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GoalsRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    private fun userId() = auth.currentUser?.uid ?: ""

    fun userRef() = db.collection("users").document(userId())

    private fun goalsRef() = userRef().collection("goals")


    // ================================
    // CREATE GOAL
    // ================================
    suspend fun addGoal(goal: Goal) {
        val doc = goalsRef().document()
        val newGoal = goal.copy(id = doc.id)
        doc.set(newGoal).await()
    }


    // ================================
    // GET LIST GOALS
    // ================================
    suspend fun getGoals(): List<Goal> {
        return goalsRef()
            .orderBy("createdAt")
            .get()
            .await()
            .toObjects(Goal::class.java)
    }
    suspend fun deleteGoal(goalId: String) {
        try {
            val uid = auth.currentUser?.uid ?: return
            val userRef = db.collection("users").document(uid)
            val goalRef = userRef.collection("goals").document(goalId)
            goalRef.delete().await()
        } catch (e: Exception) {
            Log.e("GoalsRepository", "Error deleting goal: ${e.message}", e)
        }
    }


    // ================================
    // UPDATE GOAL STATUS
    // ================================
    suspend fun markGoalCompleted(goalId: String) {
        goalsRef().document(goalId)
            .update("isCompleted", true)
            .await()
    }


    // ================================
    // CONVERT GOAL TO TRANSACTION
    // ================================
    suspend fun convertGoalToTransaction(goal: Goal) {
        val userDoc = userRef()
            .get()
            .await()

        val currentBalance = userDoc.getLong("balance") ?: 0

        if (currentBalance < goal.targetAmount) return


        // Tambah transaksi ke array "transactions"
        val newTransaction = mapOf(
            "amount" to -goal.targetAmount,
            "category" to "Goal",
            "description" to goal.title,
            "date" to System.currentTimeMillis(),
            "type" to "PENGELUARAN"
        )

        userRef().update(
            mapOf(
                "transactions" to com.google.firebase.firestore.FieldValue.arrayUnion(newTransaction),
                "balance" to com.google.firebase.firestore.FieldValue.increment(-goal.targetAmount)
            )
        ).await()

        // Tandai goal selesai
        markGoalCompleted(goal.id)
    }


}
