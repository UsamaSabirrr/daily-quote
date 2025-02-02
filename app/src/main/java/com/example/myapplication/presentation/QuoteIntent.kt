package com.example.myapplication.presentation

import android.content.Context

sealed class QuoteIntent {
    object FetchQuote : QuoteIntent()
    data class CopyQuoteToClipBoard(val context: Context) : QuoteIntent()
    data class ChangeQuoteColor(val color: androidx.compose.ui.graphics.Color) : QuoteIntent()
}