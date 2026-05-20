package com.clockinpro.v2.data.repository

import androidx.room.withTransaction
import com.clockinpro.data.local.AppDatabase
import com.clockinpro.v2.data.backup.BackupCompletion
import com.clockinpro.v2.data.backup.BackupPayload
import com.clockinpro.v2.data.backup.BackupTarget
import com.clockinpro.v2.data.local.CompletionDao
import com.clockinpro.v2.data.local.CompletionEntity
import com.clockinpro.v2.data.local.TargetDao
import com.clockinpro.v2.data.local.TargetEntity
import com.clockinpro.v2.data.local.TargetTodayStatusRow
import com.clockinpro.v2.domain.StatsCalculator
import com.clockinpro.v2.domain.model.CompletionRecord
import com.clockinpro.v2.domain.model.ReminderConfig
import com.clockinpro.v2.domain.model.SaveTargetRequest
import com.clockinpro.v2.domain.model.Target
import com.clockinpro.v2.domain.model.TargetDetail
import com.clockinpro.v2.domain.model.TargetSummary
import com.clockinpro.v2.util.DateKeyUtils
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@Singleton
class TargetRepository @Inject constructor(
    private val database: AppDatabase,
    private val targetDao: TargetDao,
    private val completionDao: CompletionDao
) {
    fun observeTargetSummaries(today: LocalDate = DateKeyUtils.today()): Flow<List<TargetSummary>> {
        val dateKey = DateKeyUtils.toDateKey(today)
        return targetDao.observeTargetsForDate(dateKey).map { rows ->
            rows.map { row ->
                TargetSummary(
                    target = row.target.toDomain(),
                    isCompletedToday = row.todayCompletionId != null,
                    completedAt = row.todayCompletedAt
                )
            }
        }
    }

    fun observeTargetDetail(
        targetId: Long,
        today: LocalDate = DateKeyUtils.today()
    ): Flow<TargetDetail?> {
        return combine(
            targetDao.observeTarget(targetId),
            completionDao.observeCompletionsForTarget(targetId)
        ) { target, completions ->
            target?.let { entity ->
                val records = completions.map { completion ->
                    CompletionRecord(
                        id = completion.id,
                        targetId = completion.targetId,
                        date = DateKeyUtils.parseDateKey(completion.dateKey),
                        completedAt = completion.completedAt
                    )
                }
                TargetDetail(
                    target = entity.toDomain(),
                    stats = StatsCalculator.calculate(
                        completionDateKeys = completions.map { it.dateKey },
                        today = today
                    ),
                    completions = records
                )
            }
        }
    }

    suspend fun getTarget(targetId: Long): Target? = targetDao.getTarget(targetId)?.toDomain()

    suspend fun saveTarget(request: SaveTargetRequest): Long {
        val now = System.currentTimeMillis()
        val existing = request.id?.let { targetDao.getTarget(it) }
        val entity = TargetEntity(
            id = request.id ?: 0,
            name = request.name.trim(),
            iconKey = request.iconKey,
            colorKey = request.colorKey,
            reminderEnabled = request.reminder.enabled,
            reminderHour = request.reminder.hour,
            reminderMinute = request.reminder.minute,
            createdAt = existing?.createdAt ?: now,
            sortOrder = existing?.sortOrder ?: (targetDao.getMaxSortOrder() + 1)
        )

        return if (request.id == null) {
            targetDao.insertTarget(entity)
        } else {
            targetDao.updateTarget(entity)
            request.id
        }
    }

    suspend fun deleteTarget(targetId: Long) {
        targetDao.deleteTarget(targetId)
    }

    suspend fun markComplete(targetId: Long, date: LocalDate = DateKeyUtils.today()): Boolean {
        val dateKey = DateKeyUtils.toDateKey(date)
        val existing = completionDao.getCompletion(targetId, dateKey)
        if (existing != null) {
            return false
        }

        completionDao.upsertCompletion(
            CompletionEntity(
                targetId = targetId,
                dateKey = dateKey,
                completedAt = System.currentTimeMillis()
            )
        )
        return true
    }

    suspend fun unmarkComplete(targetId: Long, date: LocalDate = DateKeyUtils.today()) {
        completionDao.deleteCompletion(targetId, DateKeyUtils.toDateKey(date))
    }

    suspend fun exportBackup(): BackupPayload {
        val targets = targetDao.getAllTargets().map { target ->
            BackupTarget(
                id = target.id,
                name = target.name,
                iconKey = target.iconKey,
                colorKey = target.colorKey,
                reminderEnabled = target.reminderEnabled,
                reminderHour = target.reminderHour,
                reminderMinute = target.reminderMinute,
                createdAt = target.createdAt,
                sortOrder = target.sortOrder
            )
        }
        val completions = completionDao.getAllCompletions().map { completion ->
            BackupCompletion(
                id = completion.id,
                targetId = completion.targetId,
                dateKey = completion.dateKey,
                completedAt = completion.completedAt
            )
        }

        return BackupPayload(
            exportedAt = Instant.now().toString(),
            targets = targets,
            completions = completions
        )
    }

    suspend fun importBackup(payload: BackupPayload) {
        database.withTransaction {
            completionDao.clearAll()
            targetDao.clearAll()
            targetDao.insertAll(payload.targets.map { target ->
                TargetEntity(
                    id = target.id,
                    name = target.name,
                    iconKey = target.iconKey,
                    colorKey = target.colorKey,
                    reminderEnabled = target.reminderEnabled,
                    reminderHour = target.reminderHour,
                    reminderMinute = target.reminderMinute,
                    createdAt = target.createdAt,
                    sortOrder = target.sortOrder
                )
            })
            completionDao.insertAll(payload.completions.map { completion ->
                CompletionEntity(
                    id = completion.id,
                    targetId = completion.targetId,
                    dateKey = completion.dateKey,
                    completedAt = completion.completedAt
                )
            })
        }
    }

    suspend fun getReminderTargets(): List<Target> = targetDao.getReminderTargets().map(TargetEntity::toDomain)
}

private fun TargetEntity.toDomain(): Target = Target(
    id = id,
    name = name,
    iconKey = iconKey,
    colorKey = colorKey,
    reminder = ReminderConfig(
        enabled = reminderEnabled,
        hour = reminderHour,
        minute = reminderMinute
    ),
    createdAt = createdAt,
    sortOrder = sortOrder
)
