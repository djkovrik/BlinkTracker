package com.sedsoftware.blinktracker.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sedsoftware.blinktracker.database.model.BlinksRecordDbModel
import kotlinx.coroutines.flow.Flow

@Dao
internal interface BlinkTrackerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: BlinksRecordDbModel)

    @Query("SELECT * FROM stats")
    fun get(): Flow<List<BlinksRecordDbModel>>
}
