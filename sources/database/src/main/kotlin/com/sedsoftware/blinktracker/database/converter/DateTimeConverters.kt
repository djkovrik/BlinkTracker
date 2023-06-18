package com.sedsoftware.blinktracker.database.converter

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime

internal class DateTimeConverters {

    @TypeConverter
    fun fromLocalDateTime(from: LocalDateTime): String = from.toString()

    @TypeConverter
    fun toLocalDateTime(from: String): LocalDateTime = from.toLocalDateTime()
}
