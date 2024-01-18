package com.example.assistant.network

import com.example.assistant.models.ChatCompletion
import com.example.assistant.models.ChatResponse
import com.example.assistant.models.Models
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Streaming
import java.util.concurrent.TimeUnit


private const val BASE_URL = "https://api.openai.com/v1/"

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

private val converter = json.asConverterFactory(MediaType.get("application/json"))

private val client = OkHttpClient.Builder()
    .readTimeout(20, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .client(client)
    .baseUrl(BASE_URL)
    .addConverterFactory(converter)
    .build()

interface RetrofitService {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    suspend fun postChatCompletion(
        @Header("Authorization") authorization: String,
        @Body requestBody: ChatCompletion
    ): ChatResponse

    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    @Streaming
    suspend fun streamChatCompletion(
        @Header("Authorization") authorization: String,
        @Body requestBody: ChatCompletion
    ): ResponseBody

    @GET("models")
    suspend fun getModels(@Header("Authorization") authorization: String): Models
}

object OpenAIService {
    val retrofitService: RetrofitService by lazy {
        retrofit.create(RetrofitService::class.java)
    }
}