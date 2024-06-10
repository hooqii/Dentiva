package com.example.dentiva.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dentiva.data.remote.response.DoctorDetails
import com.example.dentiva.data.remote.repository.DoctorRepository
import kotlinx.coroutines.launch

class DoctorViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DoctorRepository(application)

    fun getDoctorDetails(latitude: Double, longitude: Double): LiveData<Result<List<DoctorDetails>>> {
        val result = MutableLiveData<Result<List<DoctorDetails>>>()
        viewModelScope.launch {
            result.postValue(repository.getDoctorDetails(latitude, longitude).value)
        }
        return result
    }

    fun insertDoctorDetails(doctorDetailsList: List<DoctorDetails>) {
        viewModelScope.launch {
            repository.insertDoctorDetails(doctorDetailsList)
        }
    }
}
