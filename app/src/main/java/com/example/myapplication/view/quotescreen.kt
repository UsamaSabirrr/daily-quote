package com.example.myapplication.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.Quote

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




@Composable
fun QuoteScreen(quoteColor:Color, changeQuoteColor:(color:Color)->Unit,copyQuoteToClipBoard:()->Unit) {
    var showBottomSheet by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB8E6B8))  // Light mint green background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Section with Progress
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Progress",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "1/5",
                        color = Color.Black
                    )
                }

                // Crown icon at top right
                Icon(
                    imageVector = Icons.Default.Star,  // You can replace with a crown icon
                    contentDescription = "Crown",
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp)
                )
            }

            // Quote Text
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally){
                    Text(
                        text = buildAnnotatedString {
                            append("${quotesList[0].title}")
                        },
                        textAlign = TextAlign.Center,
                        color = quoteColor,

                        fontSize = 34.sp,
                        lineHeight = 32.sp,
                        fontFamily = FontFamily.Default  // You can replace with custom font

                    )
                    Text(
                        text = buildAnnotatedString {
                            append("- ${quotesList[0].author}")
                        },
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        lineHeight = 32.sp,
                        fontFamily = FontFamily.Default  // You can replace with custom font
                    )
                }


            }

            // Bottom Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { /* Handle category click */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "General",
                        color = Color.Black
                    )
                }

                Icon(
                    imageVector = Icons.Default.Share,  // Replace with your custom share icon
                    contentDescription = "Share",
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp)
                )

                Icon(
                    imageVector = Icons.Default.Person,  // Replace with your custom profile icon
                    contentDescription = "Profile",
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp).clickable {
                            showBottomSheet = !showBottomSheet
                        }
                )
                bottomsheet( showBottomSheet, onDismiss = {
                    showBottomSheet = !showBottomSheet
                },changeQuoteColor = changeQuoteColor,copyQuoteToClipBoard=copyQuoteToClipBoard)
            }
        }
    }
}

