package com.example.dentiva.view.main

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dentiva.R
import com.example.dentiva.databinding.ActivityMainBinding
import com.example.dentiva.view.article.ArticleFragment
import com.example.dentiva.view.profile.ProfileFragment
import com.example.dentiva.view.scan.ScanFragment
import com.example.dentiva.viewmodel.SharedViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedViewModel: SharedViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dashboard -> {
                    loadFragment(DashboardFragment())
                    true
                }

                R.id.scan -> {
                    loadFragment(ScanFragment())
                    true
                }

                R.id.article -> {
                    loadFragment(ArticleFragment())
                    true
                }

                R.id.profile -> {
                    loadFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }
        binding.bottomNavigationView.selectedItemId = R.id.dashboard
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun notifyLocationPermissionGranted() {
        sharedViewModel.setRefreshData(true)
    }
}
