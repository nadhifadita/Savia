package com.example.savia_finalproject.viewmodel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.savia_finalproject.data.repository.GoalsRepository
import com.example.savia_finalproject.viewmodel.GoalsViewModel

class GoalsViewModelFactory(
    private val repo: GoalsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalsViewModel::class.java)) {
            return GoalsViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

