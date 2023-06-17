package com.sedsoftware.blinktracker.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "stats")
class BlinksRecordDbModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0L,
    @ColumnInfo(name = "blinks") val blinks: Int,
    @ColumnInfo(name = "date") val date: LocalDateTime,
)
