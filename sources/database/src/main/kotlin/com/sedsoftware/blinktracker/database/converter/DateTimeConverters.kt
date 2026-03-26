package com.sedsoftware.blinktracker.database.converter

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDateTime

internal class DateTimeConverters {

    @TypeConverter
    fun fromLocalDateTime(from: LocalDateTime): String = from.toString()

    @TypeConverter
    fun toLocalDateTime(from: String): LocalDateTime = LocalDateTime.parse(from)
}
