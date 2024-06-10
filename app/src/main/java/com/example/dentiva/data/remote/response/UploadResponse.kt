package com.example.dentiva.data.remote.response

import java.io.Serializable

data class UploadResponse(
    val jenis_penyakit: String,
    val saran: String,
    val tingkat_akurat: String
) : Serializable
