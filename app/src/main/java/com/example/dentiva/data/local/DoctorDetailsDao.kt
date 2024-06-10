package com.example.dentiva.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dentiva.data.remote.response.DoctorDetails

@Dao
interface DoctorDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(doctors: List<DoctorDetails>)

    @Query("SELECT * FROM doctor_details")
    fun getAllDoctors(): LiveData<List<DoctorDetails>>
}
