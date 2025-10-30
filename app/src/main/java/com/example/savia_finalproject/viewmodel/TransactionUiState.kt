package com.example.savia_finalproject.viewmodel

import androidx.lifecycle.ViewModel
import com.example.savia_finalproject.data.model.Transaction

data class TransactionUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

