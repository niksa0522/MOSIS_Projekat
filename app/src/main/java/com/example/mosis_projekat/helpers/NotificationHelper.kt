package com.example.mosis_projekat.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mosis_projekat.R
import com.example.mosis_projekat.activities.MainActivity
import java.util.*

object NotificationHelper {
    fun sendNotification(context:Context,name:String?){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannelWorkshops(context)
        }

        val notificationIntent = Intent(context, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            0, notificationIntent, 0
        )

        val notification = NotificationCompat.Builder(context, "Workshop Channel")
            .setContentTitle("Workshop Nearby")
            .setContentText("Workshop: ${name} is nearby")
            .setSmallIcon(R.drawable.ic_baseline_bookmark_24)
            .setContentIntent(pendingIntent)
            .build()
        val notificationID = Random().nextInt(3000)
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationID, notification)

        }

    }
    private fun createNotificationChannelWorkshops(context: Context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "Workshop Channel", "Workshop Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }
}