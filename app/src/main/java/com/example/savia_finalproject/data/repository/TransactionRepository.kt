package com.example.savia_finalproject.data.repository

import com.example.savia_finalproject.data.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TransactionRepository {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    fun addTransaction(transaction: Transaction) {
        _transactions.value = _transactions.value + transaction
    }
}
