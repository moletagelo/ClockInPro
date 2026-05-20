package com.clockinpro.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.clockinpro.v2.data.repository.TargetRepository
import com.clockinpro.v2.reminder.TargetReminderScheduler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject lateinit var repository: TargetRepository
    @Inject lateinit var reminderScheduler: TargetReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    reminderScheduler.reschedule(repository.getReminderTargets())
                }
                pendingResult.finish()
            }
        }
    }
}
