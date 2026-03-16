package com.example.vcs_project3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class InstallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_PACKAGE_ADDED) {
            val packageName = intent.data?.schemeSpecificPart
            showNotification(context, packageName)
        }
    }

    private fun showNotification(context: Context, appName: String?) {
        val channelId = "install_channel"

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Install Notification",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("App Installed")
            .setContentText("New app installed: $appName")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}