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

class ForegroundService : Service() {

    private lateinit var screenReceiver: ScreenReceiver

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var packageManagerRef: PackageManager
    private val installedPackages = HashSet<String>()
    private val interval = 5000L
    private var receiverRegistered = false

    override fun onCreate() {
        super.onCreate()
        Log.d("SERVICE_TEST", "ForegroundService created")
        createNotificationChannels()
        startForegroundServiceNotification()
        registerScreenReceiver()
        initInstalledApps()
        startCheckingInstalledApps()
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

    private fun initInstalledApps() {
        packageManagerRef = packageManager

        val apps = packageManagerRef.getInstalledApplications(0)

        for (app in apps) {
            installedPackages.add(app.packageName)
        }
    }

    private fun startCheckingInstalledApps() {
        handler.postDelayed(object : Runnable {

            override fun run() {
                val apps = packageManagerRef.getInstalledApplications(0)

                val currentPackages = HashSet<String>()

                for (app in apps) {
                    currentPackages.add(app.packageName)
                }

                installedPackages.retainAll(currentPackages)
                for (app in apps) {
                    if (!installedPackages.contains(app.packageName)) {
                        installedPackages.add(app.packageName)
                        Log.d("INSTALL_TEST", "New app detected: ${app.packageName}")
                        showInstallNotification(app.packageName)
                    }
                }

                handler.postDelayed(this, interval)
            }

        }, interval)
    }

    private fun showInstallNotification(packageName: String) {
        val manager =
            getSystemService(NotificationManager::class.java)
        val appInfo = packageManager.getApplicationInfo(packageName, 0)
        val appName = packageManager.getApplicationLabel(appInfo).toString()

        val notification = NotificationCompat.Builder(this, "install_channel")
            .setContentTitle("App Installed")
            .setContentText("New app installed: $appName")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()

        manager.notify(packageName.hashCode(), notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenReceiver)
        handler.removeCallbacksAndMessages(null)
        Log.d("SERVICE_TEST", "ForegroundService destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}