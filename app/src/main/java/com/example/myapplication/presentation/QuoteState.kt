package com.example.myapplication.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import com.example.myapplication.domain.Quote
import com.example.myapplication.network.QuoteLocal

val quotesList = listOf(
    Quote(
        id = "1",
        title = "Be the change you wish to see in the world.",
        author = "Mahatma Gandhi"
    ),
    Quote(
        id = "2",
        title = "In the middle of difficulty lies opportunity.",
        author = "Albert Einstein"
    ),
    Quote(
        id = "3",
        title = "The only way to do great work is to love what you do.",
        author = "Steve Jobs"
    ),
    Quote(
        id = "4",
        title = "Life is what happens when you're busy making other plans.",
        author = "John Lennon"
    ),
    Quote(
        id = "5",
        title = "Success is not final, failure is not fatal: it is the courage to continue that counts.",
        author = "Winston Churchill"
    ),
    Quote(
        id = "6",
        title = "The future belongs to those who believe in the beauty of their dreams.",
        author = "Eleanor Roosevelt"
    ),
    Quote(
        id = "7",
        title = "It always seems impossible until it's done.",
        author = "Nelson Mandela"
    ),
    Quote(
        id = "8",
        title = "The journey of a thousand miles begins with one step.",
        author = "Lao Tzu"
    ),
    Quote(
        id = "9",
        title = "Knowledge is being aware of what you can do. Wisdom is knowing when not to do it.",
        author = "Anonymous"
    ),
    Quote(
        id = "10",
        title = "The only limit to our realization of tomorrow will be our doubts of today.",
        author = "Franklin D. Roosevelt"
    ),
    Quote(
        id = "11",
        title = "Do what you can, with what you have, where you are.",
        author = "Theodore Roosevelt"
    ),
    Quote(
        id = "12",
        title = "Everything you've ever wanted is on the other side of fear.",
        author = "George Addair"
    ),
    Quote(
        id = "13",
        title = "Success is walking from failure to failure with no loss of enthusiasm.",
        author = "Winston Churchill"
    ),
    Quote(
        id = "14",
        title = "The best way to predict the future is to create it.",
        author = "Peter Drucker"
    ),
    Quote(
        id = "15",
        title = "If you want to lift yourself up, lift up someone else.",
        author = "Booker T. Washington"
    ),
    Quote(
        id = "16",
        title = "The only impossible journey is the one you never begin.",
        author = "Tony Robbins"
    ),
    Quote(
        id = "17",
        title = "What you get by achieving your goals is not as important as what you become by achieving your goals.",
        author = "Zig Ziglar"
    ),
    Quote(
        id = "18",
        title = "The mind is everything. What you think you become.",
        author = "Buddha"
    ),
    Quote(
        id = "19",
        title = "The way to get started is to quit talking and begin doing.",
        author = "Walt Disney"
    ),
    Quote(
        id = "20",
        title = "Don't watch the clock; do what it does. Keep going.",
        author = "Sam Levenson"
    )
)

data class QuoteState(
    val quote: Quote? = Quote(
        id = "3",
        title = "The only way to do great work is to love what you do.",
        author = "Steve Jobs"
    ),
    val quoteList:List<Quote>? = quotesList,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDailyQuoteScheduled: Boolean = false,
    val quoteColor:Color = Color.Black,
    val quoteBackground:Color = Color.Black
)