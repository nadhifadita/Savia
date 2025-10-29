package com.example.savia_finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savia_finalproject.data.model.Transaction
import com.example.savia_finalproject.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: TransactionRepository = TransactionRepository()
) : ViewModel() {

    // expose as StateFlow for UI consumption
    val transactions = repository.items

    fun addTransaction(t: Transaction) {
        viewModelScope.launch {
            repository.add(t)
        }
    }
}
