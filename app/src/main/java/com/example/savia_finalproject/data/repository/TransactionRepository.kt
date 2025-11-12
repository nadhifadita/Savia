package com.example.savia_finalproject.data.repository

import com.example.savia_finalproject.data.model.Transaction
import com.example.savia_finalproject.data.model.TransactionType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Date

class TransactionRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Mengembalikan Flow<List<Transaction>> yang mendengarkan dokumen user
     * dan mem-parsing field "transactions" yang berupa List<Map<String, Any>>.
     *
     * Ini sesuai dengan cara `TransactionBottomSheet` yang kamu kirim:
     * - BottomSheet menyimpan transaksi di field "transactions" pada dokumen user
     *   (bukan di subcollection).
     */
    fun getTransactions(): Flow<List<Transaction>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val userRef = db.collection("users").document(uid)
        val listener = userRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Bila error, kirim list kosong (atau bisa juga close())
                trySend(emptyList())
                return@addSnapshotListener
            }

            val txList = mutableListOf<Transaction>()

            try {
                val raw = snapshot?.get("transactions")
                if (raw is List<*>) {
                    raw.forEach { item ->
                        try {
                            if (item is Map<*, *>) {
                                // Ambil field dengan aman
                                val map = item as Map<String, Any?>

                                // id: ada kemungkinan tidak disimpan; gunakan date atau timestamp sebagai fallback
                                val idFromMap = (map["id"] as? Number)?.toLong()
                                val dateMillisFromMap = (map["date"] as? Number)?.toLong()
                                    ?: (map["date"] as? com.google.firebase.Timestamp)?.toDate()?.time

                                val idValue = idFromMap ?: dateMillisFromMap ?: Date().time

                                // type: disimpan sebagai String "PEMASUKAN" / "PENGELUARAN"
                                val typeStr = (map["type"] as? String) ?: "PENGELUARAN"
                                val type = try {
                                    TransactionType.valueOf(typeStr)
                                } catch (e: Exception) {
                                    TransactionType.PENGELUARAN
                                }

                                val description = (map["description"] as? String) ?: ""
                                val amount = (map["amount"] as? Number)?.toDouble() ?: 0.0
                                val category = (map["category"] as? String) ?: ""

                                val date = if (dateMillisFromMap != null) {
                                    Date(dateMillisFromMap)
                                } else {
                                    // Jika date adalah Timestamp object (rare), konversi:
                                    val ts = map["date"]
                                    if (ts is com.google.firebase.Timestamp) ts.toDate() else Date()
                                }

                                val tx = Transaction(
                                    id = idValue,
                                    type = type,
                                    description = description,
                                    amount = amount,
                                    category = category,
                                    date = date
                                )
                                txList.add(tx)
                            }
                        } catch (e: Exception) {
                            // skip item yang bermasalah, jangan crash
                        }
                    }
                }
            } catch (e: Exception) {
                // jika parsing overall gagal, kirim list kosong
            }

            trySend(txList)
        }

        awaitClose { listener.remove() }
    }
}
