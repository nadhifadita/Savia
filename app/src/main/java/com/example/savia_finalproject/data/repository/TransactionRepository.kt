package com.example.savia_finalproject.data.repository

import com.example.savia_finalproject.data.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TransactionRepository {
    private val _items = MutableStateFlow<List<Transaction>>(emptyList())
    val items: StateFlow<List<Transaction>> = _items

    fun add(transaction: Transaction) {
        _items.value = _items.value + transaction
    }
}
