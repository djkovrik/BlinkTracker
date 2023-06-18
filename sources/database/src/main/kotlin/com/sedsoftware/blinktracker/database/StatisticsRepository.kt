package com.sedsoftware.blinktracker.database

import android.content.Context
import androidx.room.Room
import com.sedsoftware.blinktracker.database.dao.BlinkTrackerDao
import com.sedsoftware.blinktracker.database.db.BlinkTrackerDatabase
import com.sedsoftware.blinktracker.database.model.BlinksRecordDbModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock.System
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface StatisticsRepository {
    suspend fun insert(count: Int)
    suspend fun observe(): Flow<List<BlinksRecordDbModel>>
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

    override suspend fun insert(count: Int) =
        withContext(Dispatchers.IO) {
            dao.insert(mapStatRecord(count))
        }

    override suspend fun observe(): Flow<List<BlinksRecordDbModel>> =
        withContext(Dispatchers.IO) {
            dao.get()
        }

    private fun mapStatRecord(count: Int): BlinksRecordDbModel =
        BlinksRecordDbModel(blinks = count, date = System.now().toLocalDateTime(TimeZone.currentSystemDefault()))

}
