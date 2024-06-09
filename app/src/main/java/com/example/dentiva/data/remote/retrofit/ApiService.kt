package com.example.dentiva.data.remote.retrofit

import com.example.dentiva.data.local.DoctorDetails
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("places")
    suspend fun getDoctorsDetails(@Body location: JsonObject): Response<List<DoctorDetails>>
}
