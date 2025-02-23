package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.work.WorkManager
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.QuoteMapper
import com.example.myapplication.data.QuoteRepositoryImpl
import com.example.myapplication.domain.Quote
import com.example.myapplication.domain.QuoteRepository
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.presentation.QuoteIntent
import com.example.myapplication.presentation.QuoteViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.view.QuoteScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    val workManager = WorkManager.getInstance()

    private val USER_PREFERENCES_NAME = "user_preferences"
     val dataStore by preferencesDataStore(
        name = USER_PREFERENCES_NAME
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "quote-database"
            ).build()
            val api = RetrofitClient.apiService
            val quoteMapper = QuoteMapper()
            val quoteRepository = QuoteRepositoryImpl(db,api,quoteMapper)
            val viewModel = QuoteViewModel(workManager,quoteRepository)

            val state by viewModel.state.collectAsState()
            val scope = CoroutineScope(Dispatchers.Main)
            val context = LocalContext.current


             MyApplicationTheme{
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            QuoteScreen(
                                viewModel = viewModel,
                                dataStore,
                                quoteColor = state.quoteColor, changeQuoteColor = { color ->
                                viewModel.processIntent(QuoteIntent.ChangeQuoteColor(color))
                            }, copyQuoteToClipBoard = {

                            viewModel.processIntent(
                                QuoteIntent.CopyQuoteToClipBoard(
                                    context = context,
                                )
                            )
                            },
                            )
//                            Button(onClick = {
//                                val config = Configuration.Builder()
//                                    .setMinimumLoggingLevel(Log.DEBUG)
//                                    .setExecutor(SynchronousExecutor())
//                                    .build()
//
//                                WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
//
//                                val testWorker = PeriodicWorkRequestBuilder<QuoteWorker>(
//                                    1, TimeUnit.MINUTES  // Much shorter interval for testing
//                                ).build()
//                                workManager.enqueue(testWorker)
//                            }) {
//                                Text("Click me")
//                            }
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

