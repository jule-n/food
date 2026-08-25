package com.jule.food.feature_locations.data

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

object Converters {
    @TypeConverter
    fun stringListToJson(list: List<String>): String {
        return Json.encodeToString(list)
    }
    @TypeConverter
    fun jsonToStringList(json: String): List<String> {
        return Json.decodeFromString<List<String>>(json)
    }
}