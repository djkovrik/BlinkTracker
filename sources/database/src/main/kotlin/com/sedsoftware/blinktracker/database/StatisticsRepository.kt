package com.sedsoftware.blinktracker.database

import android.content.Context
import androidx.room.Room
import com.sedsoftware.blinktracker.database.dao.BlinkTrackerDao
import com.sedsoftware.blinktracker.database.db.BlinkTrackerDatabase
import com.sedsoftware.blinktracker.database.model.BlinksRecordDbModel
import com.sedsoftware.blinktracker.database.model.StatRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface StatisticsRepository {
    suspend fun insert(record: StatRecord)
    suspend fun observe(): Flow<List<StatRecord>>
}

class StatisticsRepositoryImpl(
    private val context: Context,
) : StatisticsRepository {

    private val db: BlinkTrackerDatabase by lazy {
        Room.databaseBuilder(context, BlinkTrackerDatabase::class.java, BlinkTrackerDatabase.NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    private val dao: BlinkTrackerDao by lazy {
        db.getBlinkTrackerDao()
    }

    override suspend fun insert(record: StatRecord) =
        withContext(Dispatchers.IO) {
            dao.insert(mapStatRecord(record))
        }

    override suspend fun observe(): Flow<List<StatRecord>> =
        withContext(Dispatchers.IO) {
            dao.get().map(::mapDbRecord)
        }

    private fun mapStatRecord(record: StatRecord): BlinksRecordDbModel =
        BlinksRecordDbModel(blinks = record.blinks, date = record.date)

    private fun mapDbRecord(records: List<BlinksRecordDbModel>): List<StatRecord> =
        records.map {
            StatRecord(
                blinks = it.blinks,
                date = it.date,
            )
        }
}
