package com.example.dentiva.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {

    private val _isUserAuthenticated = MutableLiveData<Boolean>()
    val isUserAuthenticated: LiveData<Boolean> get() = _isUserAuthenticated

    init {
        checkUserAuthentication()
    }

    private fun checkUserAuthentication() {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        _isUserAuthenticated.value = currentUser != null
    }

    companion object {
        private var instance: AuthViewModel? = null

        fun getInstance(): AuthViewModel {
            if (instance == null) {
                instance = AuthViewModel()
            }
            return instance!!
        }
    }
}
