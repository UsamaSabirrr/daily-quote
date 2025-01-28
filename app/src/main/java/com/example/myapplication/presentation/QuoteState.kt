package com.example.myapplication.presentation

import com.example.myapplication.domain.Quote

data class QuoteState(
    val quote: Quote? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDailyQuoteScheduled: Boolean = false
)