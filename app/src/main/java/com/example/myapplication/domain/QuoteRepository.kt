package com.example.myapplication.domain

interface QuoteRepository{
    suspend fun getAllQuotesLocally():List<Quote>
    suspend fun deleteQuotes()
    suspend fun saveQuotesList(quotes:List<Quote>)
    suspend fun getAllQuotesOnline():List<Quote>
}