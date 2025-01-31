package com.example.myapplication.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import androidx.compose.ui.res.vectorResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun bottomsheet(showBottomSheet:Boolean,onDismiss:()->Unit,changeQuoteColor:(color:Color)->Unit,copyQuoteToClipBoard:()->Unit){
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val colors = listOf(Color(0xffe8daef),Color(0xffd2b4de),Color(0xffabebc6),Color(0xfff1c40f),Color(0xff8e44ad))


    if (showBottomSheet) {
        ModalBottomSheet(

            onDismissRequest = {
                onDismiss()
            },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.height(200.dp)) {
                Row {
                    Column(modifier = Modifier.clickable {
                        copyQuoteToClipBoard()
                    }) {
                        Icon(imageVector = ImageVector.vectorResource(R.drawable.baseline_content_copy_24),contentDescription = "Description")
                        Text("Copy Quote")
                    }
                }

                //Color Selection
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    for(i in 0 until 5) {
                        Box(
                            modifier = Modifier.height(60.dp).width(60.dp).border(
                                1.dp, Color.Transparent,
                                RoundedCornerShape(10.dp)
                            ).background(color = colors[i]).clickable {
                                changeQuoteColor(colors[i])
                            }
                        ) {

                        }
                        Spacer(modifier = Modifier.width(5.dp))
                    }


                }
            }
        }
    }
}