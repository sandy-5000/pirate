package com.darkube.pirate.utils

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun get(@Url url: String): JsonElement
}

object RetrofitClient {
    private const val BASE_URL = "https://darkube.onrender.com"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }
}

fun fetch(url: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val json: JsonElement = RetrofitClient.apiService.get(url)
            val message = json.jsonObject["message"]?.jsonPrimitive?.contentOrNull
            Log.d("api-result", "$message")
        } catch (e: Exception) {
            Log.d("api-error", "${e.message}")
        }
    }
}
