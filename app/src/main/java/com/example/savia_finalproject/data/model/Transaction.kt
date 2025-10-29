package com.example.savia_finalproject.data.model

import java.util.*

data class Transaction(
    val id: Int = 0,
    val type: TransactionType,
    val description: String,
    val amount: Double,
    val category: String,
    val date: Date = Date()
)

enum class TransactionType {
    PEMASUKAN,
    PENGELUARAN
}