package com.example.dentiva.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dentiva.databinding.ActivityLoginBinding
import com.example.dentiva.view.main.MainActivity
import com.example.dentiva.view.signup.SignUpActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private  lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val btnBack = binding.btnBack
        val signUp = binding.signUp

        btnBack.setOnClickListener {
            finish()
        }

        signUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edRegisterName.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.edRegisterName.error = "Email is required"
                binding.edRegisterName.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edRegisterName.error = "Please provide a valid email"
                binding.edRegisterName.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.edRegisterPassword.error = "Password is required"
                binding.edRegisterPassword.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.edRegisterPassword.error = "Password should be at least 6 characters long"
                binding.edRegisterPassword.requestFocus()
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    navigateToMainActivity()
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}