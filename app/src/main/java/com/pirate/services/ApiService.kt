package com.pirate.services

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.pirate.config.SERVER_URL
import com.pirate.types.RequestType
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
import retrofit2.http.HeaderMap
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun get(
        @Url url: String,
        @HeaderMap headers: Map<String, String> = emptyMap(),
    ): Response<JsonElement>

    @POST
    suspend fun post(
        @Url url: String,
        @Body body: JsonElement,
        @HeaderMap headers: Map<String, String> = emptyMap(),
    ): Response<JsonElement>

    @PUT
    suspend fun put(
        @Url url: String,
        @Body body: JsonElement,
        @HeaderMap headers: Map<String, String> = emptyMap(),
    ): Response<JsonElement>

    @PATCH
    suspend fun patch(
        @Url url: String,
        @Body body: JsonElement,
        @HeaderMap headers: Map<String, String> = emptyMap(),
    ): Response<JsonElement>
}


object RetrofitClient {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(SERVER_URL)
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
    headers: Map<String, String> = emptyMap(),
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val response: Response<JsonElement> = when (type) {
                RequestType.GET -> RetrofitClient.apiService.get(url = url, headers = headers)
                RequestType.POST -> RetrofitClient.apiService.post(
                    url = url,
                    body = body,
                    headers = headers
                )
                RequestType.PUT -> RetrofitClient.apiService.put(
                    url = url,
                    body = body,
                    headers = headers
                )
                RequestType.PATCH -> RetrofitClient.apiService.patch(
                    url = url,
                    body = body,
                    headers = headers
                )
            }
            if (response.isSuccessful) {
                callback(response.body() ?: JsonObject(emptyMap()))
            } else {
                callback(buildJsonObject {
                    put("error", response.body().toString())
                })
            }
        } catch (e: Exception) {
            Log.d("api-error", e.message ?: "")
            val errorResponse = buildJsonObject {
                put("error", e.message ?: "UNKNOWN_ERROR")
            }
            callback(errorResponse)
        }
    }
}
