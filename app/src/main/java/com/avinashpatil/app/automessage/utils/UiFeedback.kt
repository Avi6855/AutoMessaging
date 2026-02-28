package com.avinashpatil.app.automessage.utils

import android.app.Service
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.avinashpatil.app.automessage.R

object UiFeedback {
    /**
     * Show a custom neumorphic-style toast. Safe to call from background service.
     */
    fun showNeumorphicToast(context: Context, message: String) {
        try {
            val inflater = LayoutInflater.from(context)
            val view: View = inflater.inflate(R.layout.toast_neumorphic, null)
            val tv = view.findViewById<TextView>(R.id.toastText)
            tv.text = message

            Handler(Looper.getMainLooper()).post {
                val toast = Toast(context.applicationContext)
                toast.view = view
                toast.duration = Toast.LENGTH_SHORT
                toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 150)
                toast.show()
            }
        } catch (_: Exception) { }
    }
}