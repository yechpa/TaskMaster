package com.example.taskmaster.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromTaskType(value: TaskType): String {
        return value.name
    }

    @TypeConverter
    fun toTaskType(value: String): TaskType {
        return TaskType.valueOf(value)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
