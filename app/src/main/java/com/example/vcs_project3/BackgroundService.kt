package com.example.vcs_project3

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log

class BackgroundService : Service() {
    private lateinit var screenReceiver: ScreenReceiver

    override fun onCreate() {
        super.onCreate()
        Log.d("SERVICE_TEST", "BackgroundService created")

        screenReceiver = ScreenReceiver()

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_USER_PRESENT)

        registerReceiver(screenReceiver, filter)
        Log.d("SERVICE_TEST", "ScreenReceiver registered")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("SERVICE_TEST", "BackgroundService is running")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenReceiver)
        Log.d("SERVICE_TEST", "BackgroundService destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}