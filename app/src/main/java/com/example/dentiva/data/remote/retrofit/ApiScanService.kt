package com.example.dentiva.data.remote.retrofit

import com.example.dentiva.data.remote.response.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiScanService {
    @Multipart
    @POST("all")
    fun uploadImage(@Part file: MultipartBody.Part): Call<UploadResponse>
}
