package com.example.savia_finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savia_finalproject.data.api.NewsArticle
import com.example.savia_finalproject.data.api.ProductClient
import com.example.savia_finalproject.data.api.ProductResponse
import com.example.savia_finalproject.data.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EducationViewModel : ViewModel() {

    // State untuk Berita
    private val _newsList = MutableStateFlow<List<NewsArticle>>(emptyList())
    val newsList: StateFlow<List<NewsArticle>> = _newsList

    // State untuk Produk (BARU)
    private val _productList = MutableStateFlow<List<ProductResponse>>(emptyList())
    val productList: StateFlow<List<ProductResponse>> = _productList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchAllData()
    }

    fun fetchAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // 1. AMBIL BERITA (NewsAPI)
                launch {
                    try {
                        val apiKey = "a5ee0c10222748a590440a40712f26d1"
                        val response = RetrofitClient.instance.getNewsByQuery(
                            apiKey = apiKey,
                            query = "ekonomi OR investasi OR saham OR emas",
                            language = "id"
                        )
                        val validArticles = response.articles.filter {
                            it.urlToImage != null && !it.description.isNullOrEmpty()
                        }
                        _newsList.value = validArticles.take(10) // Batasi 10
                    } catch (e: Exception) {
                        // Error berita jangan hentikan app, log saja
                        e.printStackTrace()
                    }
                }

                // 2. AMBIL PRODUK REAL (ProductClient)
                launch {
                    try {
                        val products = ProductClient.instance.getRealProducts()
                        _productList.value = products
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat data"
            } finally {
                // Tunggu sebentar agar loading terlihat smooth (opsional)
                _isLoading.value = false
            }
        }
    }

    // Fungsi refresh manual
    fun fetchNews() {
        fetchAllData()
    }
}