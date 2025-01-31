package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import androidx.work.WorkManager
import com.example.myapplication.network.AppDatabase
import com.example.myapplication.network.QuoteRepository
import com.example.myapplication.presentation.QuoteIntent
import com.example.myapplication.presentation.QuoteViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.view.QuoteScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    val workManager = WorkManager.getInstance()
    val quoteRepository = QuoteRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "quote-database"
            ).build()
            val viewModel = QuoteViewModel(workManager,quoteRepository,db)

            val state by viewModel.state.collectAsState()
            val scope = CoroutineScope(Dispatchers.Main)

            val context = LocalContext.current

            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    QuoteScreen(quoteColor = state.quoteColor, changeQuoteColor ={color ->
                        viewModel.processIntent(QuoteIntent.ChangeQuoteColor(color))
                    }, copyQuoteToClipBoard = {
                        viewModel.processIntent(QuoteIntent.CopyQuoteToClipBoard(context = context,"This is new quote"))
                    }
                    )
                }
            }
        }
    }
}

//will need to add these changes too

//@Composable
//fun QuoteUI(state: QuoteState){
//    Column(
//        modifier = Modifier.fillMaxHeight().fillMaxWidth(),
//        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
//        if(state.isLoading){
//            CircularProgressIndicator()
//        }else {
//            Column {
//                Button(
//                    onClick = {
//                        scope.launch(Dispatchers.IO) {
//                            viewModel.processIntent(QuoteIntent.FetchQuote)
//                        }
//                    }) {
//                    Text("Fetch Latest")
//                }
//                Text(state.quote?.title ?: "")
//            }
//            Column {
//                for (i in 0 until (state.quoteList?.size ?: 0))
//                    state.quoteList?.get(i)?.quote?.let { Text(it) }
//            }
//        }
//    }
//}

