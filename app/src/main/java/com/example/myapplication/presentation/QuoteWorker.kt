package com.example.myapplication.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import kotlinx.coroutines.delay

class QuoteWorker(context: Context,
                  workerParams: WorkerParameters,):CoroutineWorker(appContext = context, params = workerParams) {
    override suspend fun doWork(): Result {
        return try {
            Log.d("TAG","Start")
            createNotification("Hello")


            // Fetch new quote
            delay(5000)
            createNotification("Bye")

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun createNotification(quote: String) {
        val context = applicationContext
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )



        // Create notification channel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                1.toString(),
                "Daily Quotes",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = quote
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(context, 1.toString())
            .setSmallIcon(R.drawable.ic_launcher_background) // Make sure you have this icon in your drawable
            .setContentTitle("Daily Quote")
            .setContentText(quote)
            .setStyle(NotificationCompat.BigTextStyle().bigText(quote))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true).setContentIntent(pendingIntent)
            .build()

        // Show the notification
        notificationManager.notify(1, notification)
    }
}