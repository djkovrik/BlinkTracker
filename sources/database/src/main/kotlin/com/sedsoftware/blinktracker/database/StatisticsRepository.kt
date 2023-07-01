package com.sedsoftware.blinktracker.database

import android.content.Context
import androidx.room.Room
import com.sedsoftware.blinktracker.database.dao.BlinkTrackerDao
import com.sedsoftware.blinktracker.database.db.BlinkTrackerDatabase
import com.sedsoftware.blinktracker.database.model.BlinksRecordDbModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock.System
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

interface StatisticsRepository {
    suspend fun insert(count: Int)
    suspend fun observe(): Flow<List<BlinksRecordDbModel>>
}

class StatisticsRepositoryReal(
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

    private val timeZone: TimeZone by lazy {
        TimeZone.currentSystemDefault()
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
        BlinksRecordDbModel(blinks = count, date = System.now().toLocalDateTime(timeZone))

}

/**
 * Generates 13 months period fake stats with blinks record at every 5 mins
 */
class StatisticsRepositoryFake: StatisticsRepository {

    private val timeZone: TimeZone by lazy {
        TimeZone.currentSystemDefault()
    }

    private val today: Instant = System.now()
    private val starting: Instant = today.minus(period = DateTimePeriod(months = FAKE_STATS_MONTHS), timeZone = timeZone)

    override suspend fun insert(count: Int) = Unit

    override suspend fun observe(): Flow<List<BlinksRecordDbModel>> =
        withContext(Dispatchers.IO) {
            flow {
                val result = mutableListOf<BlinksRecordDbModel>()
                var current: Instant = starting
                while (current < today) {
                    result.add(
                        BlinksRecordDbModel(
                            blinks = Random.nextInt(from = RAND_MIN, until = RAND_MAX),
                            date = current.toLocalDateTime(timeZone)
                        )
                    )
                    current = current.plus(period = DateTimePeriod(minutes = FAKE_STATS_PERIOD_MIN), timeZone = timeZone)
                }
                emit(result)
            }
        }


    private companion object {
        const val RAND_MIN = 12
        const val RAND_MAX = 26
        const val FAKE_STATS_MONTHS = 13
        const val FAKE_STATS_PERIOD_MIN = 5
    }
}
