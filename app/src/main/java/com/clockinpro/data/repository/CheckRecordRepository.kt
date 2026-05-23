package com.clockinpro.data.repository

import com.clockinpro.data.local.*
import com.clockinpro.domain.model.CheckRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckRecordRepository @Inject constructor(
    private val checkRecordDao: CheckRecordDao,
    private val preferencesManager: PreferencesManager
) {
    fun getAllRecords(userId: Long): Flow<List<CheckRecord>> {
        return checkRecordDao.getAllRecordsByUser(userId).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getTodayRecords(userId: Long): Flow<List<CheckRecord>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis

        return checkRecordDao.getRecordsByDate(userId, startOfDay, endOfDay).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getRecordsByDateRange(userId: Long, startTime: Long, endTime: Long): Flow<List<CheckRecord>> {
        return checkRecordDao.getRecordsByDateRange(userId, startTime, endTime).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun getLatestRecord(userId: Long): CheckRecord? {
        return checkRecordDao.getLatestRecord(userId)?.toDomain()
    }

    suspend fun insertRecord(record: CheckRecord): Long {
        return checkRecordDao.insertRecord(record.toEntity())
    }

    suspend fun updateSyncStatus(recordId: Long, status: SyncStatus) {
        checkRecordDao.updateSyncStatus(recordId, status)
    }

    suspend fun deleteRecord(record: CheckRecord) {
        checkRecordDao.deleteRecord(record.toEntity())
    }

    suspend fun getConsecutiveDays(userId: Long): Int {
        val dates = checkRecordDao.getDistinctCheckDates(userId)
        if (dates.isEmpty()) return 0

        var consecutiveDays = 0
        val calendar = Calendar.getInstance()
        var expectedDate = calendar.time

        for (dateStr in dates) {
            calendar.time = expectedDate
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            expectedDate = calendar.time

            val dateParts = dateStr.split("-")
            if (dateParts.size == 3) {
                val checkDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, dateParts[0].toInt())
                    set(Calendar.MONTH, dateParts[1].toInt() - 1)
                    set(Calendar.DAY_OF_MONTH, dateParts[2].toInt())
                }.time

                if (Math.abs(checkDate.time - dateStr.toDate().time) < 86400000) {
                    consecutiveDays++
                } else {
                    break
                }
            }
        }

        return consecutiveDays
    }

    private fun String.toDate(): java.util.Date {
        val parts = this.split("-")
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, parts[0].toInt())
            set(Calendar.MONTH, parts[1].toInt() - 1)
            set(Calendar.DAY_OF_MONTH, parts[2].toInt())
        }.time
    }

    suspend fun getTotalCheckInCount(userId: Long): Int {
        return checkRecordDao.getRecordCountByType(userId, CheckType.CHECK_IN)
    }

    suspend fun getPendingSyncCount(userId: Long): Int {
        return checkRecordDao.getPendingSyncCount(userId)
    }

    suspend fun getCheckedDates(userId: Long): Set<String> {
        return checkRecordDao.getDistinctCheckDates(userId).toSet()
    }
}
