package com.example.savia_finalproject.data.model

data class Goal(
    val id: String = "",
    val title: String = "",
    val targetAmount: Long = 0,
    val savedAmount: Long = 0,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)