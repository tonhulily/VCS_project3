package com.example.vcs_project3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat

class BackgroundService : Service() {
    private lateinit var screenReceiver: ScreenReceiver
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var packageManagerRef: PackageManager
    private val installedPackages = HashSet<String>()

    override fun onCreate() {
        super.onCreate()
        Log.d("SERVICE_TEST", "BackgroundService created")

        screenReceiver = ScreenReceiver()

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_USER_PRESENT)

        registerReceiver(screenReceiver, filter)
        Log.d("SERVICE_TEST", "ScreenReceiver registered")

        packageManagerRef = packageManager

        val apps = packageManagerRef.getInstalledApplications(0)
        for (app in apps) {
            installedPackages.add(app.packageName)
        }
        startCheckingInstalledApps()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("SERVICE_TEST", "BackgroundService is running")
        return START_STICKY
    }
    private fun startCheckingInstalledApps() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val apps = packageManagerRef.getInstalledApplications(0)
                for (app in apps) {
                    if (!installedPackages.contains(app.packageName)) {
                        installedPackages.add(app.packageName)
                        Log.d("INSTALL_TEST", "New app detected: ${app.packageName}")
                        showInstallNotification(app.packageName)
                    }
                }
                handler.postDelayed(this, 3000)
            }

        }, 3000)
    }
    private fun showInstallNotification(packageName: String) {
        val channelId = "install_channel"
        val manager =
            getSystemService(NotificationManager::class.java)

        val channel = NotificationChannel(
            channelId,
            "Install Notification",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("App Installed")
            .setContentText("New app installed: $packageName")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        manager.notify(packageName.hashCode(), notification)
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