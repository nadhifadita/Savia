package com.example.savia_finalproject.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savia_finalproject.data.model.Goal
import com.example.savia_finalproject.data.repository.GoalsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.Dispatcher

class GoalsViewModel(
    private val repository: GoalsRepository
) : ViewModel() {

    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals

    private val _balance = MutableStateFlow<Long>(0)
    val balance: StateFlow<Long> = _balance


    init {
        loadGoals()
        loadBalance()
    }


    private fun loadGoals() {
        viewModelScope.launch {
            _goals.value = repository.getGoals()
        }
    }

    private fun loadBalance() {
        viewModelScope.launch {
            val userDoc = repository.userRef().get().await()
            _balance.value = userDoc.getLong("balance") ?: 0
        }
    }


    fun addGoal(title: String, amount: Long) {
        viewModelScope.launch {
            repository.addGoal(
                Goal(
                    title = title,
                    targetAmount = amount
                )
            )
            loadGoals()
        }
    }


    fun convertGoal(goal: Goal) {
        viewModelScope.launch {
            repository.convertGoalToTransaction(goal)
            loadGoals()
            loadBalance()
        }
    }


    fun isGoalReady(goal: Goal): Boolean {
        return balance.value >= goal.targetAmount
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            if (goal.id.isNotEmpty()) {
                repository.deleteGoal(goal.id)
                loadGoals()
            } else {
                Log.w("GoalsViewModel", "Goal ID is empty. Cannot delete.")
            }
        }
    }
}
