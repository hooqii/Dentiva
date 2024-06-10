package com.example.dentiva.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doctor_details")
data class DoctorDetails(
    @PrimaryKey val displayName: String,
    val rating: Double,
    val address: String,
    val phoneNumber: String?,
    val photos: List<Photo>
)

data class Photo(
    val urlImage: String
)
