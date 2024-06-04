package com.example.dentiva.view.main

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.dentiva.databinding.ActivityMainBinding
import com.example.dentiva.util.ReminderReceiver
import com.example.dentiva.view.welcome.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val searchView: android.widget.SearchView = binding.searchView
        searchView.setIconifiedByDefault(false)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 7)  // 7 AM
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        setReminder(calendar.timeInMillis)

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setReminder(timeInMillis: Long) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun updateSmartReminderUI(calendar: Calendar) {
        val currentTime = Calendar.getInstance()
        val remainingTimeInMillis = calendar.timeInMillis - currentTime.timeInMillis
        val hours = (remainingTimeInMillis / (1000 * 60 * 60)).toInt()
        val minutes = (remainingTimeInMillis / (1000 * 60) % 60).toInt()

        val timeText = String.format("%02d hours %02d minutes", hours, minutes)
        binding.tvSmartReminderTitle.text = "Smart Reminder: Brush your teeth in $timeText"
    }

    private fun logoutUser() {
        auth.signOut()
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
