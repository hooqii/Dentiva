package com.example.dentiva.view.signup

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dentiva.databinding.ActivitySignUpBinding
import com.example.dentiva.view.login.LoginActivity
import com.example.dentiva.view.main.MainActivity
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.login.setOnClickListener {
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val name = binding.edRegisterName.text.toString().trim()
            val email = binding.edRegisterEmail.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString().trim()

            if (name.isEmpty()) {
                binding.edRegisterName.error = "Name is required"
                binding.edRegisterName.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.edRegisterEmail.error = "Email is required"
                binding.edRegisterEmail.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edRegisterEmail.error = "Please provide a valid email"
                binding.edRegisterEmail.requestFocus()
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
            registerUser(email, password)

        }

    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Check your email to verify your account",
                                Toast.LENGTH_LONG
                            ).show()
                            navigateToMainActivity()
                        } else {
                            Toast.makeText(
                                this,
                                verificationTask.exception?.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
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