package com.example.dentiva.util

import android.content.BroadcastReceiver
import android.content.Context
import android.widget.Toast
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Lakukan sesuatu saat alarm dipicu
        Toast.makeText(context, "Time to brush your teeth!", Toast.LENGTH_SHORT).show()
    }
}