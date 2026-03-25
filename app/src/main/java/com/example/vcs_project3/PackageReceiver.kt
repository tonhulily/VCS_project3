package com.example.vcs_project3

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat

class PackageReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_PACKAGE_ADDED == intent.action) {
            val packageName = intent.data?.schemeSpecificPart ?: return

            val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
            if (isReplacing) return

            Log.d("INSTALL_TEST", "New app installed: $packageName")

            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            val appName = pm.getApplicationLabel(appInfo).toString()

            val notification = NotificationCompat.Builder(context, "install_channel")
                .setContentTitle("App Installed")
                .setContentText("New app installed: $appName")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setAutoCancel(true)
                .build()

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.notify(packageName.hashCode(), notification)
        }
    }
}