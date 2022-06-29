package com.example.mosis_projekat.helpers

import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.mosis_projekat.firebase.realtimeDB.RealtimeHelper
import com.example.mosis_projekat.services.LocationService

object ServiceHelper {
    fun startService(context: Context){
        val startServiceIntent = Intent(context, LocationService::class.java)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            context.startForegroundService(startServiceIntent)
        } else {
            // Pre-O behavior.
            context.startService(startServiceIntent)
        }
    }
    fun stopService(context: Context){
        val stopServiceIntent = Intent(context, LocationService::class.java)
        context.stopService(stopServiceIntent)
        RealtimeHelper.deleteLocation()
    }
}