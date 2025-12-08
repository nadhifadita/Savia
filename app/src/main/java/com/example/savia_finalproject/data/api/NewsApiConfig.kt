package com.example.savia_finalproject.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class NewsResponse(
    val status: String,
    val articles: List<NewsArticle>
)

data class NewsArticle(
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val source: Source?
)

data class Source(val name: String)

interface NewsApiService {
    @GET("v2/everything")
    suspend fun getNewsByQuery(
        @Query("q") query: String,
        @Query("language") language: String = "id",
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("apiKey") apiKey: String
    ): NewsResponse
}

// Singleton Retrofit (Tetap sama)
object RetrofitClient {
    private const val BASE_URL = "https://newsapi.org/"

    val instance: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }
}