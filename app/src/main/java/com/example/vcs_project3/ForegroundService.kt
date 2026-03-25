package com.example.vcs_project3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class ForegroundService : Service() {

    private lateinit var screenReceiver: ScreenReceiver
    private lateinit var packageReceiver: PackageReceiver
    private var packageReceiverRegistered = false
    private var receiverRegistered = false

    override fun onCreate() {
        super.onCreate()
        Log.d("SERVICE_TEST", "ForegroundService created")
        createNotificationChannels()
        startForegroundServiceNotification()
        registerScreenReceiver()
        registerPackageReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("SERVICE_TEST", "ForegroundService running")
        return START_STICKY
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(NotificationManager::class.java)

        val serviceChannel = NotificationChannel(
            "service_channel",
            "Foreground Service",
            NotificationManager.IMPORTANCE_LOW
        )

        val installChannel = NotificationChannel(
            "install_channel",
            "Install Notification",
            NotificationManager.IMPORTANCE_HIGH
        )

        manager.createNotificationChannel(serviceChannel)
        manager.createNotificationChannel(installChannel)
    }

    private fun startForegroundServiceNotification() {
        val notification = NotificationCompat.Builder(this, "service_channel")
            .setContentTitle("VCS Project 3")
            .setContentText("Service is running")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        startForeground(1, notification)
    }
    private fun registerScreenReceiver() {
        screenReceiver = ScreenReceiver()

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_USER_PRESENT)

        if (!receiverRegistered) {
            registerReceiver(screenReceiver, filter)
            receiverRegistered = true
        }

        Log.d("SERVICE_TEST", "ScreenReceiver registered")
    }
    private fun registerPackageReceiver() {
        packageReceiver = PackageReceiver()

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addDataScheme("package")
        }

        if (!packageReceiverRegistered) {
            registerReceiver(packageReceiver, filter)
            packageReceiverRegistered = true
        }
        Log.d("SERVICE_TEST", "PackageReceiver registered")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenReceiver)
        unregisterReceiver(packageReceiver)
        Log.d("SERVICE_TEST", "ForegroundService destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}