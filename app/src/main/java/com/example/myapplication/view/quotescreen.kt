package com.example.myapplication.view

import android.app.WallpaperManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.domain.Quote
import com.example.myapplication.presentation.QuoteIntent
import com.example.myapplication.presentation.QuoteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.Stack


@Composable
fun QuoteScreen(
    viewModel: QuoteViewModel,
    quoteColor:Color, changeQuoteColor:(color:Color)->Unit,copyQuoteToClipBoard: ()->Unit) {

    val state by viewModel.state.collectAsState()
    val scrollState = rememberLazyListState()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val flingBehavior = rememberSnapFlingBehavior(
        lazyListState = scrollState,
        snapPosition = SnapPosition.Center
    )






    var showBottomSheet by remember { mutableStateOf(false) }
    var scope = CoroutineScope(Dispatchers.Main)

    var showContentForWallpapaer by remember {
        mutableStateOf(false)
    }
    var graphicsLayer = rememberGraphicsLayer()
    val context = LocalContext.current


    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.firstVisibleItemIndex }
            .distinctUntilChanged()  // Only emit when value actually changes
            .collect { index ->
                viewModel.processIntent(QuoteIntent.SetCurrentQuoteIndex(index+1))
            }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB8E6B8))  // Light mint green background
    ) {
        LazyColumn(
            state = scrollState,
            flingBehavior = flingBehavior

        ) {

            viewModel.state.value.quoteList?.size?.let {
                items(it) {index->
                    // Quote Text
                    Column(modifier = Modifier.height(screenHeight)){
                        Box(
                            modifier = Modifier
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                Text(
                                    text = buildAnnotatedString {
                                        append(viewModel.state.value.quoteList!![index].quote)
                                    },
                                    textAlign = TextAlign.Center,
                                    color = quoteColor,
                                    fontSize = 44.sp,
                                    lineHeight = 65.sp,
                                    letterSpacing = 0.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .padding(horizontal = 24.dp)
                                        .fillMaxWidth(),

                                    )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = buildAnnotatedString {
                                        append(viewModel.state.value.quoteList!![index].author)
                                    },
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                    lineHeight = 32.sp,
                                    fontFamily = FontFamily.Default,  // You can replace with custom font
                                    color = quoteColor
                                )
                            }


                        }

                    }
                }
            }
        }
        // Bottom Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp).align(Alignment.BottomEnd).padding(bottom = 40.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,

        ) {

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.outline_settings_24),  // Replace with your custom profile icon
                contentDescription = "Settings",
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp).clickable {
                        showBottomSheet = !showBottomSheet
                    }
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.outline_settings_24),  // Replace with your custom profile icon
                contentDescription = "Fetch Images",
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp).clickable {
                        scope.launch {
                            viewModel.getQuote()
                        }
                    }
            )
            bottomsheet(
                viewModel = viewModel,

                showBottomSheet,
                onDismiss = {
                    showBottomSheet = !showBottomSheet
                },
                changeQuoteColor = changeQuoteColor,
                copyQuoteToClipBoard = copyQuoteToClipBoard,
                setWallpaper = {
                    scope.launch(Dispatchers.IO) {
                        showContentForWallpapaer = true
                        //                        val bitmap = WallpaperRenderer(context).renderToBitmap(700,700,
                        //                            {
                        //                                ContentForWallpaper()
                        //                            }
                        //                        )
                        //Log.d("Tag","hello how are you $bitmap")
                        val wallpaperManager = WallpaperManager.getInstance(context)
                        // wallpaperManager.setBitmap(bitmap)
                        showContentForWallpapaer = false
                    }
                })
        }


    }

}


