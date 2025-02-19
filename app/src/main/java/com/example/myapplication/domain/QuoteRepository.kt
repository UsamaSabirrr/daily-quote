package com.example.myapplication.domain

interface QuoteRepository{
    fun getAllQuotes():List<Quote>
    fun deleteQuotes()
}