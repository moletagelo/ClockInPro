package com.clockinpro.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckRecordDao {
    @Query("SELECT * FROM check_records WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllRecordsByUser(userId: Long): Flow<List<CheckRecordEntity>>

    @Query("SELECT * FROM check_records WHERE userId = :userId AND timestamp >= :startOfDay AND timestamp < :endOfDay ORDER BY timestamp ASC")
    fun getRecordsByDate(userId: Long, startOfDay: Long, endOfDay: Long): Flow<List<CheckRecordEntity>>

    @Query("SELECT * FROM check_records WHERE userId = :userId AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getRecordsByDateRange(userId: Long, startTime: Long, endTime: Long): Flow<List<CheckRecordEntity>>

    @Query("SELECT * FROM check_records WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestRecord(userId: Long): CheckRecordEntity?

    @Query("SELECT COUNT(*) FROM check_records WHERE userId = :userId AND type = :type")
    suspend fun getRecordCountByType(userId: Long, type: CheckType): Int

    @Query("SELECT COUNT(*) FROM check_records WHERE userId = :userId AND syncStatus = :status")
    suspend fun getPendingSyncCount(userId: Long, status: SyncStatus = SyncStatus.PENDING): Int

    @Query("SELECT DISTINCT date(timestamp / 1000, 'unixepoch', 'localtime') as checkDate FROM check_records WHERE userId = :userId ORDER BY checkDate DESC")
    suspend fun getDistinctCheckDates(userId: Long): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: CheckRecordEntity): Long

    @Update
    suspend fun updateRecord(record: CheckRecordEntity)

    @Query("UPDATE check_records SET syncStatus = :status WHERE id = :recordId")
    suspend fun updateSyncStatus(recordId: Long, status: SyncStatus)

    @Delete
    suspend fun deleteRecord(record: CheckRecordEntity)

    @Query("DELETE FROM check_records WHERE userId = :userId")
    suspend fun deleteAllRecordsByUser(userId: Long)
}
