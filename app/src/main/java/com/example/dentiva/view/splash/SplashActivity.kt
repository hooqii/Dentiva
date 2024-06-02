package com.example.dentiva.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.ViewModelProvider
import com.example.dentiva.R
import com.example.dentiva.view.main.MainActivity
import com.example.dentiva.view.welcome.WelcomeActivity
import com.example.dentiva.viewmodel.AuthViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val motionLayout = findViewById<MotionLayout>(R.id.motionLayout)

        motionLayout.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                navigateToNextScreen()
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }
        })

        Handler(Looper.getMainLooper()).postDelayed({
            motionLayout.transitionToEnd()
        }, 2000L)
    }

    private fun navigateToNextScreen() {
        authViewModel.isUserAuthenticated.observe(this) { isAuthenticated ->
            val nextActivity = if (isAuthenticated) {
                MainActivity::class.java
            } else {
                WelcomeActivity::class.java
            }
            val intent = Intent(this, nextActivity)
            startActivity(intent)
            finish()
        }
    }
}
