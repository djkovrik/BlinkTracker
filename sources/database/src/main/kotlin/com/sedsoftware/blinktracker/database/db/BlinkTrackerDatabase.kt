package com.sedsoftware.blinktracker.database.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sedsoftware.blinktracker.database.converter.DateTimeConverters
import com.sedsoftware.blinktracker.database.dao.BlinkTrackerDao
import com.sedsoftware.blinktracker.database.model.BlinksRecordDbModel

@Database(
    entities = [
        BlinksRecordDbModel::class
    ],
    version = BlinkTrackerDatabase.VERSION,
)
@TypeConverters(
    DateTimeConverters::class
)
internal abstract class BlinkTrackerDatabase : RoomDatabase() {
    companion object {
        const val NAME = "blink_tracker.db"
        const val VERSION = 1
    }

    abstract fun getBlinkTrackerDao(): BlinkTrackerDao
}
