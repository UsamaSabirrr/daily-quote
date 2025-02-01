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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.launch

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
                MyApplicationTheme{
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                        Column {
                            QuoteScreen(quoteColor = state.quoteColor, changeQuoteColor = { color ->
                                viewModel.processIntent(QuoteIntent.ChangeQuoteColor(color))
                            }, copyQuoteToClipBoard = {

                                // val bitmap = composableToBitmap(700.dp,700.dp, content = {QuoteWallpaperContent(Color(0xFF93000A))},  )

//                            val bitmap = createBitmapFromComposable(context) {
//                                QuoteWallpaperContent(Color(0xFF93000A))
//                            }
                                //  Log.d("Tag","bit map is $bitmap")
//                            scope.launch(Dispatchers.IO) {
//                                try {
//                                    val wallpaperManager = WallpaperManager.getInstance(context)
//                                    wallpaperManager.setBitmap(bitmap)
//                                } catch (e: Exception) {
//                                    e.printStackTrace()
//                                }
//                            }

                                //TODO uncomment
//                            viewModel.processIntent(
//                                QuoteIntent.CopyQuoteToClipBoard(
//                                    context = context,
//                                    "This is new quote"
//                                )
//                            )
                            },


                            )
                            dummyData()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun dummyData(){
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .drawWithContent {
                // call record to capture the content in the graphics layer
                graphicsLayer.record {
                    // draw the contents of the composable into the graphics layer
                    this@drawWithContent.drawContent()
                }

            }
            .clickable {
                coroutineScope.launch {
                    val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                    Log.d("Tag","hello how are you $bitmap")
                    val wallpaperManager = WallpaperManager.getInstance(context)
                    wallpaperManager.setBitmap(bitmap)


                    // do something with the newly acquired bitmap
                }
            }
            .background(Color.White)
    ) {
        Text("Hello Android", fontSize = 26.sp)
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

