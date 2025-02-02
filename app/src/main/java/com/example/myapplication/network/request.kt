package com.example.myapplication.network

import android.util.Log
import com.example.myapplication.domain.Quote
import com.example.myapplication.domain.QuoteOnline
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.UUID

interface ApiService {
    @GET("quotes")
    suspend fun getQuote(): List<QuoteOnline>

}

object RetrofitClient {
    private const val BASE_URL = "https://zenquotes.io/api/"

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

class QuoteRepository(val database: AppDatabase){
    private val apiService = RetrofitClient.apiService


    suspend fun getUser():Result<List<Quote>>{
        try {

          val result =  apiService.getQuote()
           val final = result.map { it->
               val uuid:String = UUID.randomUUID().toString()
                Quote(id=uuid, quote = it.q, author = it.a)
            }
            print("result is $result")
            Log.d("User","data is $result")
            return Result.success(final)
        }catch (e:Exception){
            print(e)
            return Result.failure(e)
        }
    }

    suspend fun saveQuotesLocally(quoteList:List<Quote>){
        val quoteLocalList = quoteList.map { it->QuoteLocal(id = it.id, quote = it.quote, author = it.author?:"") }
        database.quoteDao().insertQuote(quoteLocalList)
    }

    suspend fun getAllQuotes():List<Quote>{
        val quotes = database.quoteDao().getAll()
       val quotesList = quotes.map { it->Quote(id = it.id, quote = it.quote?:"", author = it.author) }
        return quotesList

    }

}