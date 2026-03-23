package com.example.vcs_project3

import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

class ScreenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SCREEN_ON -> {
                Log.d("SCREEN_TEST", "Screen ON detected")
                showMessage(context)
            }
        }
    }

    private fun showMessage(context: Context) {
        val windowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val messageTextView = TextView(context).apply {
            text = context.getString(R.string.screen_message)
            textSize = 16f
            setTextColor(Color.WHITE)
            setBackgroundResource(R.drawable.message_box)
            setPadding(60, 40, 60, 40)
            elevation = 12f
            compoundDrawablePadding = 20
        }

        ContextCompat.getDrawable(context, R.drawable.ic_notifications)?.let {
            val wrapped = DrawableCompat.wrap(it)
            DrawableCompat.setTint(wrapped, Color.WHITE)

            messageTextView.setCompoundDrawablesWithIntrinsicBounds(
                wrapped, null, null, null
            )
        }

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            y = 200
        }

        windowManager.addView(messageTextView, layoutParams)

        messageTextView.alpha = 0f
        messageTextView.translationY = 150f

        messageTextView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(250)
            .start()

        Handler(Looper.getMainLooper()).postDelayed({
            val slideDown =
                ObjectAnimator.ofFloat(messageTextView, "translationY", 0f, 200f)
            slideDown.duration = 300
            slideDown.start()

            messageTextView.animate()
                .alpha(0f)
                .setDuration(300)
                .start()

            Handler(Looper.getMainLooper()).postDelayed({
                messageTextView.let {
                    try {
                        windowManager.removeView(it)
                    } catch (_: Exception) {}
                }
            }, 300)
        }, 3000)
    }
}