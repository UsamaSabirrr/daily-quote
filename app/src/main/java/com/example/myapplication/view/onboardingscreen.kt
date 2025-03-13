package com.example.myapplication.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.presentation.QuoteViewModel

@Composable
fun OnboardingScreent(onBoardingDone:()->Unit,viewModel: QuoteViewModel){
    val state by viewModel.state.collectAsState()
    var sliderPosition by remember { mutableStateOf(3f) }
    var openAlertDialog by remember { mutableStateOf(true) }
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize().padding(20.dp)) {

        if (openAlertDialog){
            RulesDialog({
                openAlertDialog = false
            }) { }
        }

        Text("How many quotes can you handle", style = MaterialTheme.typography.bodyLarge)

        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it
                            viewModel.quotesCount = it.toInt()},
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 50,
            valueRange = 3f..50f
        )
        Text(text = sliderPosition.toInt().toString())
        Button(
            onClick = onBoardingDone,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(12.dp)
        ) {
            Text(text = "Let the Quotes Flow", style = MaterialTheme.typography.labelLarge)
        }

    }
}


    @Composable
    fun RulesDialog(onConfirmation: () -> Unit, onDismissRequest: () -> Unit) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)

            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Filled.Notifications,
                        contentDescription = "Notification Icon",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Rules",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "You need to identify the quotes you read and only then you will be able to fetch new quotes. (can't let wisdom go to waste)",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = onConfirmation,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Text(text = "Ok", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
