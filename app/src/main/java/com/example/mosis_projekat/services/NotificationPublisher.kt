package com.example.mosis_projekat.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.mosis_projekat.helpers.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class NotificationPublisher : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if ((Intent.ACTION_BOOT_COMPLETED).equals(intent.getAction())) {
            // reset all alarms
            //pokupi sve objekte iz baze, prodji kroz sve i za svaki koji ima alarm set setuj alarm
        } else {


            // perform your scheduled task here (eg. send alarm notification)
            NotificationHelper.sendNotification(
                context,
                intent.getStringExtra("text"),
            )
        }
    }
}