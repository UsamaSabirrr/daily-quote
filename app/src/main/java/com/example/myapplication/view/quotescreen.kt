package com.example.myapplication.view

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.myapplication.presentation.QuoteIntent
import com.example.myapplication.presentation.QuoteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first

object PreferencesKeys {
    val SCROLL_INDEX = intPreferencesKey("scroll_index")
}

@Composable
fun QuoteScreen(
    viewModel: QuoteViewModel,
    dataStore: DataStore<Preferences>,
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
    val context = LocalContext.current


    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.firstVisibleItemIndex }
            .distinctUntilChanged()  // Only emit when value actually changes
            .collect { index ->
                viewModel.processIntent(QuoteIntent.SetCurrentQuoteIndex(index))
                dataStore.edit(transform = {it->
                    it[PreferencesKeys.SCROLL_INDEX] = index
                })
            }
    }

    LaunchedEffect(Unit) {
        Log.d("Tag","only once")
        dataStore.data.first().let { preferences ->  // Using .first() to get only initial value
            val savedIndex = preferences[PreferencesKeys.SCROLL_INDEX] ?: 0
            scrollState.animateScrollToItem(savedIndex)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB8E6B8)).pointerInput(Unit){
                detectTapGestures {
                    Log.d("Quote","check input")
                    scope.launch(Dispatchers.IO) {
                        viewModel.visible()
                    }

                }
            }) // Light mint green background
     {
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
        AnimatedVisibility(
            visible = viewModel.state.value.isVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text("${(viewModel.state.value.quoteIndex+1).toString()}/ ${(viewModel.state.value.quoteList?.size ?: 0).toString()}",modifier = Modifier.align(Alignment.TopStart).padding(top = 40.dp, start = 20.dp), color = Color.Black)

        }        // Bottom Buttons
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
                           // viewModel.getQuote()
                        }
                    }
            )
            Bottomsheet(
                viewModel = viewModel,
                showBottomSheet,
                onDismiss = {
                    showBottomSheet = !showBottomSheet
                },
                changeQuoteColor = changeQuoteColor,
                )
        }

    }
}


