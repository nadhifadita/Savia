package com.example.savia_finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savia_finalproject.data.model.Transaction
import com.example.savia_finalproject.data.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map // <-- Import map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Date
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

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val dummyTransactions = listOf(
                Transaction(1, TransactionType.PEMASUKAN, "Gaji Oktober", 5000000.0, "Gaji", Date()),
                Transaction(2, TransactionType.PENGELUARAN, "Makan Siang", 25000.0, "Makanan", Date()),
                Transaction(3, TransactionType.PENGELUARAN, "Langganan Spotify", 55000.0, "Hiburan", Date()),
                Transaction(4, TransactionType.PENGELUARAN, "Beli Bensin", 100000.0, "Transportasi", Date()),
                Transaction(5, TransactionType.PEMASUKAN, "Proyek Freelance", 1500000.0, "Freelance", Date())
            )

            val sortedTransactions = dummyTransactions.sortedBy { it.date }

            _uiState.value = TransactionUiState(
                transactions = sortedTransactions,
                isLoading = false
            )
        }
    }
}
