package com.darkube.pirate.utils

import com.darkube.pirate.types.RequestType
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun get(@Url url: String): Response<JsonElement>

    @POST
    suspend fun post(@Url url: String, @Body body: JsonElement): Response<JsonElement>
}

object RetrofitClient {
    private const val BASE_URL = "https://the-pirate-api.onrender.com"

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

fun fetch(
    url: String,
    callback: (response: JsonElement) -> Unit,
    type: RequestType = RequestType.GET,
    body: JsonElement = JsonObject(emptyMap()),
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response: Response<JsonElement> = when (type) {
                RequestType.GET -> RetrofitClient.apiService.get(url)
                RequestType.POST -> RetrofitClient.apiService.post(url, body)
            }
            if (response.isSuccessful) {
                callback(response.body() ?: JsonObject(emptyMap()))
            } else {
                callback(buildJsonObject {
                    put("error", "__ERROR__")
                })
            }
        } catch (e: Exception) {
            val errorResponse = buildJsonObject {
                put("error", e.message ?: "UNKNOWN_ERROR")
            }
            callback(errorResponse)
        }
    }
}
