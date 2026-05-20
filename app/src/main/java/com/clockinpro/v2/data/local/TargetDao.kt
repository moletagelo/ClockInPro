package com.clockinpro.v2.data.local

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

data class TargetTodayStatusRow(
    @Embedded val target: TargetEntity,
    @ColumnInfo(name = "todayCompletionId") val todayCompletionId: Long?,
    @ColumnInfo(name = "todayCompletedAt") val todayCompletedAt: Long?
)

@Dao
interface TargetDao {
    @Query(
        """
        SELECT t.*, c.id AS todayCompletionId, c.completedAt AS todayCompletedAt
        FROM targets t
        LEFT JOIN completions c
            ON t.id = c.targetId AND c.dateKey = :dateKey
        ORDER BY t.sortOrder ASC, t.createdAt ASC
        """
    )
    fun observeTargetsForDate(dateKey: String): Flow<List<TargetTodayStatusRow>>

    @Query("SELECT * FROM targets WHERE id = :targetId")
    fun observeTarget(targetId: Long): Flow<TargetEntity?>

    @Query("SELECT * FROM targets WHERE id = :targetId")
    suspend fun getTarget(targetId: Long): TargetEntity?

    @Query("SELECT * FROM targets ORDER BY sortOrder ASC, createdAt ASC")
    suspend fun getAllTargets(): List<TargetEntity>

    @Query("SELECT * FROM targets WHERE reminderEnabled = 1 ORDER BY sortOrder ASC, createdAt ASC")
    suspend fun getReminderTargets(): List<TargetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarget(target: TargetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(targets: List<TargetEntity>)

    @Update
    suspend fun updateTarget(target: TargetEntity)

    @Query("DELETE FROM targets WHERE id = :targetId")
    suspend fun deleteTarget(targetId: Long)

    @Query("SELECT COALESCE(MAX(sortOrder), -1) FROM targets")
    suspend fun getMaxSortOrder(): Int

    @Query("DELETE FROM targets")
    suspend fun clearAll()
}
