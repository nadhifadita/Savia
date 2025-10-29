package com.example.savia_finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savia_finalproject.data.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map // <-- Import map
import kotlinx.coroutines.flow.stateIn
import java.text.NumberFormat
import java.util.Locale

class TransactionViewModel : ViewModel() {
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    // --- GANTI KODE DI BAWAH INI ---

    // StateFlow untuk total pemasukan
    val totalIncome: StateFlow<Double> = transactions.map { txs ->
        txs.filter { it.amount > 0 }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // StateFlow untuk total pengeluaran
    val totalExpense: StateFlow<Double> = transactions.map { txs ->
        txs.filter { it.amount < 0 }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // StateFlow untuk saldo total (pemasukan + pengeluaran)
    val totalBalance: StateFlow<Double> = combine(totalIncome, totalExpense) { income, expense ->
        income + expense
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // --- AKHIR DARI KODE YANG DIGANTI ---

    fun addTransaction(transaction: Transaction) {
        _transactions.value = _transactions.value + transaction
    }

    // Helper function untuk format mata uang (opsional tapi sangat berguna)
    fun formatCurrency(amount: Double): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.maximumFractionDigits = 0 // Menghilangkan desimal
        return numberFormat.format(amount)
    }
}
