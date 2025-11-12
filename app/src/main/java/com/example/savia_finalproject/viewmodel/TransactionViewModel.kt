package com.example.savia_finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savia_finalproject.data.model.Transaction
import com.example.savia_finalproject.data.repository.TransactionRepository
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TransactionViewModel : ViewModel() {
    private val repository = TransactionRepository()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    init {
        // Collect dari repository (yang mendengarkan dokumen user)
        viewModelScope.launch {
            repository.getTransactions().collect { list ->
                // pastikan urutan, dsb. disini kita langsung set ke stateflow
                _transactions.value = list
            }
        }
    }

    /**
     * IMPORTANT:
     * Karena TransactionBottomSheet (versi sekarang) sudah menulis transaksi langsung ke Firestore,
     * kita tidak melakukan lagi write di sini untuk menghindari duplikat atau konflik.
     *
     * Jika kamu ingin semua penulisan dikelola lewat ViewModel (lebih bersih), beri tahu aku
     * supaya aku pindahkan logic runTransaction ke sini dan ubah BottomSheet untuk memanggil viewModel.addTransaction(tx)
     */
    fun addTransaction(transaction: Transaction) {
        // no-op: Firestore write dilakukan oleh BottomSheet (sesuai kode yang kamu kirim)
        // Tapi tetap bisa tambahkan ke local state agar UI bereaksi lebih cepat, Firestore snapshot listener akan meng-override.
        _transactions.value = _transactions.value + transaction
    }

    // total income
    val totalIncome: StateFlow<Double> = transactions.map { txs ->
        txs.filter { it.amount > 0 }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // total expense
    val totalExpense: StateFlow<Double> = transactions.map { txs ->
        txs.filter { it.amount < 0 }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // total balance
    val totalBalance: StateFlow<Double> = combine(totalIncome, totalExpense) { income, expense ->
        income + expense
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // weekly chart data (sama logika seperti sebelumnya, tapi memakai _transactions)
    val weeklyChartData: StateFlow<Pair<List<Entry>, List<String>>> = transactions.map { txs ->
        val dayFormat = SimpleDateFormat("E", Locale("in", "ID"))
        val last7Days = (0..6).map {
            val date = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -it) }.time
            dayFormat.format(date)
        }.reversed()

        val dailyTotals = last7Days.associateWith { 0.0 }.toMutableMap()
        val sevenDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -6); set(Calendar.HOUR_OF_DAY, 0) }.time
        val recentTxs = txs.filter { it.date.after(sevenDaysAgo) }

        recentTxs.forEach { tx ->
            val dayKey = dayFormat.format(tx.date)
            if (dailyTotals.containsKey(dayKey)) {
                dailyTotals[dayKey] = dailyTotals.getValue(dayKey) + tx.amount
            }
        }

        val entries = dailyTotals.values.mapIndexed { index, total ->
            Entry(index.toFloat(), total.toFloat())
        }

        Pair(entries, last7Days)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(emptyList(), emptyList()))

    // helper format
    fun formatCurrency(amount: Double): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.maximumFractionDigits = 0
        return numberFormat.format(amount)
    }
}
