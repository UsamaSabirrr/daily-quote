package com.example.myapplication.view

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun bottomsheet(showBottomSheet:Boolean,onDismiss:()->Unit,changeQuoteColor:(color:Color)->Unit,copyQuoteToClipBoard:()->Unit,setWallpaper:()->Unit){
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val colors = listOf(Color(0xffe8daef),Color(0xffd2b4de),Color(0xffabebc6),Color(0xfff1c40f),Color(0xff8e44ad))
    var mygraphicsdLayer = rememberGraphicsLayer()
    val context = LocalContext.current

    if (showBottomSheet) {
        ModalBottomSheet(
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = {
                onDismiss()
            },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier
                .height(200.dp)
                .padding(horizontal = 10.dp)) {
                Row {
                   BottomSheetActions(drawable = R.drawable.baseline_content_copy_24,"Copy Quote", onClick = copyQuoteToClipBoard)
                    BottomSheetActions(drawable = R.drawable.baseline_wallpaper_24,"Set Wallpaper", onClick = {
                        scope.launch(Dispatchers.IO) {
                            if(mygraphicsdLayer.size.width>0 && mygraphicsdLayer.size.height>0){
                                val bitmap = mygraphicsdLayer.toImageBitmap().asAndroidBitmap()
                                checkPermissionAndSave(context,bitmap)
//                                // create file
//                                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
//                                    "my-app-post-${System.currentTimeMillis()}.png")
//
//                                // write bitmap to file as PNG
//                                file.outputStream().use { out ->
//                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
//                                    out.flush()
//                                }
//                                Log.d("Tag","saved successfully")
                            }
                        }
                    })

                }

                Spacer(modifier = Modifier.height(10.dp))

                //Color Selection
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    for(i in 0 until 5) {
                        Box(
                            modifier = Modifier
                                .height(50.dp)
                                .width(50.dp)
                                .background(color = colors[i])
                                .clickable {
                                    changeQuoteColor(colors[i])
                                }
                                .border(
                                    1.dp, Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                        ) {
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                    }


                }
                ContentForWallpaper(onValueChange = {graphicsdLayer ->
                    mygraphicsdLayer = graphicsdLayer
                })
            }
        }
    }
}

fun saveImageToGallery(context: Context, bitmap: Bitmap) {
    val filename = "my-app-post-${System.currentTimeMillis()}.png"
    var fos: OutputStream? = null

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // For Android 10 (Q) and above, use MediaStore
        context.contentResolver?.also { resolver ->
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { resolver.openOutputStream(it) }
        }
    } else {
        // For Android versions below 10 (Q), use the old method (deprecated but still works)
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, filename)
        fos = FileOutputStream(image)
    }

    fos?.use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        Log.d("Tag", "Image saved to gallery successfully")
    } ?: Log.e("Tag", "Failed to save image to gallery")
}

// Example usage (assuming you have a Bitmap called 'bitmap'):
// Check for permissions before calling this function
fun checkPermissionAndSave(context: Context, bitmap: Bitmap) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // No permissions needed for Android 10 and above when saving to MediaStore
        saveImageToGallery(context, bitmap)
    } else {
        // For Android versions below 10, we need to request WRITE_EXTERNAL_STORAGE permission
       // if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            saveImageToGallery(context, bitmap)
       // } else {
            // Request permission
       //     ActivityCompat.requestPermissions(context as android.app.Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
       // }
    }
}

@Composable
fun BottomSheetActions(drawable: Int, actionName:String, onClick:()->Unit){
    Column(
        modifier = Modifier.clickable {
            onClick()
        }, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = ImageVector.vectorResource(drawable),contentDescription = actionName, tint = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(10.dp))
        Text("Copy Quote", color = MaterialTheme.colorScheme.onSurface)
    }
}


@Composable
fun ContentForWallpaper(onValueChange:(graphicsLayer:GraphicsLayer)->Unit){
    var graphicsLayer = rememberGraphicsLayer()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    Box(modifier = Modifier
        .size(0.dp) // size 0 so that no space is used in the UI
        .drawWithCache {
            // draw to graphics layer
            graphicsLayer = obtainGraphicsLayer().apply {
                record(
                    size = IntSize(
                        width = screenWidthDp.toPx().toInt(),
                        height = screenHeightDp.toPx().toInt()
                    )
                ) {
                    drawContent()
                }
            }
            onValueChange(graphicsLayer)
            // leave blank to skip drawing on the screen
            onDrawWithContent { }
        }) {
        Box(
            // override the parent size with desired size of the recording
            modifier = Modifier
                .wrapContentHeight(unbounded = true, align = Alignment.Top)
                .wrapContentWidth(unbounded = true, align = Alignment.Start)
                .requiredSize(width = screenWidthDp, height = screenHeightDp)
        ) {

                        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,modifier = Modifier.width(screenWidthDp).height(screenHeightDp).background(color = Color.Cyan)) {
                            Text(
                                text = buildAnnotatedString {
                                   append("Heelo bro how are you? Where have you been")
                                },
                                textAlign = TextAlign.Center,
                                color = Color(0xFFFFB4AB),
                                fontSize = 28.sp,
                                lineHeight = 40.sp,
                                letterSpacing = 0.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                )
                            Text(
                                text = buildAnnotatedString {
                                    append("- ${quotesList[0].author}")
                                },
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                lineHeight = 32.sp,
                                fontFamily = FontFamily.Default,  // You can replace with custom font
                                color = Color(0xFFFFB4AB)
                            )
                    }
                }
            }
        }




