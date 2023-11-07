package com.example.assistant.network

import com.example.assistant.BuildConfig
import com.example.assistant.models.ChatCompletion
import com.example.assistant.models.ChatResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


private const val BASE_URL = "https://api.openai.com/v1/"

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

private val converter = json.asConverterFactory(MediaType.get("application/json"))

private val client = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .build()
        chain.proceed(newRequest)
    }
    .build()

private val retrofit = Retrofit.Builder()
    .client(client)
    .baseUrl(BASE_URL)
    .addConverterFactory(converter)
    .build()

interface RetrofitService {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    suspend fun postChatCompletions(@Body requestBody: ChatCompletion): ChatResponse
}

object OpenAIService {
    val retrofitService: RetrofitService by lazy {
        retrofit.create(RetrofitService::class.java)
    }
}