package com.example.savia_finalproject.data.api

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class ProductResponse(
    val name: String,
    val rate: String,
    val risk: String,
    val type: String,
    val url: String
) {
    fun getIcon(): ImageVector {
        return when (type) {
            "SBN" -> Icons.Default.Flag
            "GOLD" -> Icons.Default.MonetizationOn
            "BANK" -> Icons.Default.AccountBalance
            "STOCK" -> Icons.Default.TrendingUp
            else -> Icons.Default.AccountBalance
        }
    }
}

interface ProductApiService {
    @GET("f4ed1799bdbe86cd84d3")
    suspend fun getRealProducts(): List<ProductResponse>
}

object ProductClient {
    private const val BASE_URL = "https://api.npoint.io/"
    val instance: ProductApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductApiService::class.java)
    }
}