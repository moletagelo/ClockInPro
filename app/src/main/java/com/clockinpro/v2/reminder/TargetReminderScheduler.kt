package com.clockinpro.v2.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.clockinpro.v2.domain.model.Target
import com.clockinpro.worker.ReminderReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TargetReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(target: Target) {
        cancel(target.id)
        if (!target.reminder.enabled) {
            return
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            nextTriggerMillis(target.reminder.hour, target.reminder.minute),
            AlarmManager.INTERVAL_DAY,
            reminderPendingIntent(target.id, target.name)
        )
    }

    fun cancel(targetId: Long) {
        alarmManager.cancel(reminderPendingIntent(targetId, ""))
    }

    fun reschedule(targets: List<Target>) {
        targets.forEach(::schedule)
    }

    private fun reminderPendingIntent(targetId: Long, targetName: String): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_TARGET_ID, targetId)
            putExtra(ReminderReceiver.EXTRA_TARGET_NAME, targetName)
        }
        return PendingIntent.getBroadcast(
            context,
            targetId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun nextTriggerMillis(hour: Int, minute: Int): Long {
        var trigger = LocalDateTime.now()
            .withHour(hour)
            .withMinute(minute)
            .withSecond(0)
            .withNano(0)
        if (trigger.isBefore(LocalDateTime.now())) {
            trigger = trigger.plusDays(1)
        }
        return trigger.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
