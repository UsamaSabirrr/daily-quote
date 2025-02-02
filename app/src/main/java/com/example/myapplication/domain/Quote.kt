package com.example.myapplication.domain

import retrofit2.http.GET


data class Quote(
    val id: String,
    val quote: String,
    val author:String? = null
)

data class QuoteOnline(
    val q:String,
    val a:String
)
