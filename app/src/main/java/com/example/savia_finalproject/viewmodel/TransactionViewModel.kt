package com.example.savia_finalproject.viewmodel

import androidx.compose.foundation.layout.add
import androidx.compose.ui.autofill.ContentDataType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savia_finalproject.data.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.flow.map // <-- Import map
import kotlinx.coroutines.flow.stateIn
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.text.format

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

    // StateFlow untuk data chart saldo mingguan
    val weeklyChartData: StateFlow<Pair<List<Entry>, List<String>>> = transactions.map { txs ->
        val calendar = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("E", Locale("in", "ID")) // "Sen", "Sel", dst.

        // Buat daftar 7 hari terakhir, dari 6 hari lalu (paling kiri) ke hari ini (paling kanan)
        val last7Days = (0..6).map {
            val date = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -it) }.time
            dayFormat.format(date)
        }.reversed()

        // Buat map untuk menampung total transaksi per hari
        val dailyTotals = last7Days.associateWith { 0.0 }.toMutableMap()

        // Filter transaksi dalam 7 hari terakhir
        val sevenDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -6); set(Calendar.HOUR_OF_DAY, 0) }.time
        val recentTxs = txs.filter { it.date.after(sevenDaysAgo) }

        // Akumulasikan jumlah transaksi per hari
        recentTxs.forEach { tx ->
            val dayKey = dayFormat.format(tx.date)
            if (dailyTotals.containsKey(dayKey)) {
                dailyTotals[dayKey] = dailyTotals.getValue(dayKey) + tx.amount
            }
        }

        // Buat daftar `Entry` untuk pustaka grafik
        val entries = dailyTotals.values.mapIndexed { index, total ->
            Entry(index.toFloat(), total.toFloat())
        }

        // Kembalikan data dan labelnya
        Pair(entries, last7Days)

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(emptyList(), emptyList()))


    // Helper function untuk format mata uang (opsional tapi sangat berguna)
    fun formatCurrency(amount: Double): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.maximumFractionDigits = 0 // Menghilangkan desimal
        return numberFormat.format(amount)
    }
}
