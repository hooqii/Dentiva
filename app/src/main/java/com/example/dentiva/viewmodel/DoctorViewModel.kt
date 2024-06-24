package com.example.dentiva.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dentiva.data.remote.response.DoctorEntity
import com.example.dentiva.data.remote.repository.DoctorRepository
import kotlinx.coroutines.launch

class DoctorViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DoctorRepository(application)

    fun getDoctorDetails(latitude: Double, longitude: Double): LiveData<Result<List<DoctorEntity>>> {
        val result = MutableLiveData<Result<List<DoctorEntity>>>()
        viewModelScope.launch {
            result.postValue(repository.getDoctorDetails(latitude, longitude).value)
        }
        return result
    }

    fun insertDoctorDetails(doctorEntityList: List<DoctorEntity>) {
        viewModelScope.launch {
            repository.insertDoctorDetails(doctorEntityList)
        }
    }
}
