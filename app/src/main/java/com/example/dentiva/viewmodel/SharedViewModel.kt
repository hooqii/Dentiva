package com.example.dentiva.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _refreshData = MutableLiveData<Boolean>()
    val refreshData: LiveData<Boolean> get() = _refreshData

    fun setRefreshData(value: Boolean) {
        _refreshData.value = value
    }
}
