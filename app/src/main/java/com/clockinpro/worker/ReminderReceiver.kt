package com.clockinpro.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.clockinpro.ClockInApp
import com.clockinpro.ui.MainActivity

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val targetId = intent.getLongExtra(EXTRA_TARGET_ID, NOTIFICATION_ID.toLong())
        val targetName = intent.getStringExtra(EXTRA_TARGET_NAME) ?: "your target"

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            targetId.hashCode(),
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ClockInApp.REMINDER_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("Time to check in")
            .setContentText("Keep your momentum going for $targetName.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(targetId.hashCode(), notification)
    }

    companion object {
        const val NOTIFICATION_ID = 1001
        const val EXTRA_TARGET_ID = "extra_target_id"
        const val EXTRA_TARGET_NAME = "extra_target_name"
    }
}
