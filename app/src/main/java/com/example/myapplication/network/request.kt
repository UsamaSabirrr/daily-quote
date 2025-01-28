package com.example.myapplication.network

import android.util.Log
import com.example.myapplication.domain.Quote
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("todos/3")
    suspend fun getQuote():Quote

}

object RetrofitClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    var loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit by lazy {
        // 1. Create the builder
        val builder = Retrofit.Builder()

        // 2. Set the base URL for all API calls
        builder.baseUrl(BASE_URL)

        // 3. Set the HTTP client we created (with logging)
        builder.client(okHttpClient)

        // 4. Add Gson converter to convert JSON to Kotlin objects
        builder.addConverterFactory(GsonConverterFactory.create())

        // 5. Build and return the Retrofit instance
        builder.build()
    }

    val apiService:ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }


}

class QuoteRepository{
    private val apiService = RetrofitClient.apiService

    suspend fun getUser():Result<Quote>{
        try {
          val result =  apiService.getQuote()
            print("result is $result")
            Log.d("User","data is $result")
            return Result.success(result)
        }catch (e:Exception){
            print(e)
            return Result.failure(e)
        }
    }

}