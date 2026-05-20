package com.clockinpro.v2.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CompletionDao {
    @Query("SELECT * FROM completions WHERE targetId = :targetId ORDER BY dateKey DESC")
    fun observeCompletionsForTarget(targetId: Long): Flow<List<CompletionEntity>>

    @Query("SELECT * FROM completions WHERE targetId = :targetId AND dateKey = :dateKey LIMIT 1")
    suspend fun getCompletion(targetId: Long, dateKey: String): CompletionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCompletion(completion: CompletionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(completions: List<CompletionEntity>)

    @Query("DELETE FROM completions WHERE targetId = :targetId AND dateKey = :dateKey")
    suspend fun deleteCompletion(targetId: Long, dateKey: String)

    @Query("SELECT * FROM completions ORDER BY completedAt DESC")
    suspend fun getAllCompletions(): List<CompletionEntity>

    @Query("DELETE FROM completions")
    suspend fun clearAll()
}
