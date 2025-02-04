package com.example.myapplication

import android.app.WallpaperManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.room.Room
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.impl.utils.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.myapplication.network.AppDatabase
import com.example.myapplication.network.QuoteRepository
import com.example.myapplication.presentation.QuoteIntent
import com.example.myapplication.presentation.QuoteViewModel
import com.example.myapplication.presentation.QuoteWorker
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.view.QuoteScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

class MainActivity : ComponentActivity() {
    val workManager = WorkManager.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "quote-database"
            ).build()
            val quoteRepository = QuoteRepository(db)
            val viewModel = QuoteViewModel(workManager,quoteRepository,db)

            val state by viewModel.state.collectAsState()
            val scope = CoroutineScope(Dispatchers.Main)

            val context = LocalContext.current

            MyApplicationTheme {
                MyApplicationTheme{
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                        Column {
//                            QuoteScreen(
//                                viewModel = viewModel,
//                                quoteColor = state.quoteColor, changeQuoteColor = { color ->
//                                viewModel.processIntent(QuoteIntent.ChangeQuoteColor(color))
//                            }, copyQuoteToClipBoard = {
//
//                            viewModel.processIntent(
//                                QuoteIntent.CopyQuoteToClipBoard(
//                                    context = context,
//                                )
//                            )
//                            },
//
//                            )
                            Button(onClick = {
                                val config = Configuration.Builder()
                                    .setMinimumLoggingLevel(Log.DEBUG)
                                    .setExecutor(SynchronousExecutor())
                                    .build()

                                WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

                                val testWorker = PeriodicWorkRequestBuilder<QuoteWorker>(
                                    1, TimeUnit.MINUTES  // Much shorter interval for testing
                                ).build()
                                workManager.enqueue(testWorker)
                            }) {
                                Text("Click me")
                            }
                        }
                    }
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

