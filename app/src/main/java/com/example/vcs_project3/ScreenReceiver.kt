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

    companion object {
        private var messageView: TextView? = null
    }

    override fun onReceive(context: Context, intent: Intent) {

        when (intent.action) {

            Intent.ACTION_SCREEN_ON -> {
                Log.d("SCREEN_TEST", "Screen ON detected")
                showMessage(context)
            }

            Intent.ACTION_USER_PRESENT -> {
                Log.d("SCREEN_TEST", "User unlocked phone")
                showMessage(context)
            }
        }
    }

    private fun showMessage(context: Context) {

        val windowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val textView = TextView(context)

        textView.text = "Screen has been turned ON"
        textView.textSize = 16f
        textView.setTextColor(Color.WHITE)

        textView.setBackgroundResource(R.drawable.message_box)
        textView.setPadding(60, 40, 60, 40)

        textView.elevation = 12f
        val icon = ContextCompat.getDrawable(context, R.drawable.ic_notifications)

        icon?.let {
            val wrapped = DrawableCompat.wrap(it)
            DrawableCompat.setTint(wrapped, Color.WHITE)

            textView.setCompoundDrawablesWithIntrinsicBounds(
                wrapped,
                null,
                null,
                null
            )
        }

        textView.compoundDrawablePadding = 20

        messageView?.let {
            try {
                windowManager.removeView(it)
            } catch (_: Exception) {}
        }

        messageView = textView

        val layoutType =
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        params.y = 200

        windowManager.addView(textView, params)

        textView.alpha = 0f
        textView.translationY = 150f

        textView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(250)
            .start()

        Handler(Looper.getMainLooper()).postDelayed({

            val slideDown =
                ObjectAnimator.ofFloat(textView, "translationY", 0f, 200f)
            slideDown.duration = 300
            slideDown.start()

            textView.animate()
                .alpha(0f)
                .setDuration(300)
                .start()

            Handler(Looper.getMainLooper()).postDelayed({

                messageView?.let {
                    try {
                        windowManager.removeView(it)
                    } catch (_: Exception) {}
                }

                messageView = null

            }, 300)

        }, 3000)
    }
}