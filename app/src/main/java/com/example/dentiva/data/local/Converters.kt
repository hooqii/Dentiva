package com.example.dentiva.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromPhotoList(value: List<Photo>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toPhotoList(value: String): List<Photo> {
        val listType = object : TypeToken<List<Photo>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
