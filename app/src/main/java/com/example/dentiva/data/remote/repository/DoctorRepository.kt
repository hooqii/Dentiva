package com.example.dentiva.data.remote.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dentiva.data.local.AppDatabase
import com.example.dentiva.data.local.DoctorDetails
import com.example.dentiva.data.local.DoctorDetailsDao
import com.example.dentiva.data.remote.retrofit.RetrofitInstance
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DoctorRepository(application: Application) {
    private val doctorDetailsDao: DoctorDetailsDao =
        AppDatabase.getDatabase(application).doctorDetailsDao()

    suspend fun getDoctorDetails(
        latitude: Double, longitude: Double
    ): LiveData<Result<List<DoctorDetails>>> {
        val result = MutableLiveData<Result<List<DoctorDetails>>>()

        val location = JsonObject().apply {
            addProperty("latitude", latitude)
            addProperty("longitude", longitude)
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getDoctorsDetails(location)
                if (response.isSuccessful && response.body() != null) {
                    val doctors = response.body()!!
                    doctorDetailsDao.insertAll(doctors)
                    result.postValue(Result.success(doctors))
                } else {
                    result.postValue(Result.failure(Throwable("Failed to fetch data from server")))
                }
            } catch (e: Exception) {
                result.postValue(Result.failure(e))
            }
            result
        }
    }

    suspend fun insertDoctorDetails(doctorDetailsList: List<DoctorDetails>) {
        doctorDetailsDao.insertAll(doctorDetailsList)
    }
}
