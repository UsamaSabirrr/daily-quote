package com.example.myapplication.presentation

import androidx.compose.ui.graphics.Color
import com.example.myapplication.domain.Quote
import com.example.myapplication.network.QuoteLocal

data class QuoteState(
    val quote: Quote? = null,
    val quoteList:List<QuoteLocal>? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDailyQuoteScheduled: Boolean = false,
    val quoteColor:Color = Color.Black
)