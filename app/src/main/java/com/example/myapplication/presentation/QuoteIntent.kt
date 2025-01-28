package com.example.myapplication.presentation

sealed class QuoteIntent {
    object FetchQuote : QuoteIntent()

//    object ScheduleDailyQuote : QuoteIntent()
//    object CancelDailyQuote : QuoteIntent()
}