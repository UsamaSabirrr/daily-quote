package com.example.myapplication.presentation

sealed class QuoteIntent {
    object FetchQuote : QuoteIntent()
    data class ChangeQuoteColor(val color: androidx.compose.ui.graphics.Color) : QuoteIntent()
}