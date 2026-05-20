package com.clockinpro.v2.data.backup

import org.junit.Assert.assertEquals
import org.junit.Test

class BackupCodecTest {

    @Test
    fun `round trips backup payload as json`() {
        val payload = BackupPayload(
            exportedAt = "2026-05-20T08:30:00Z",
            targets = listOf(
                BackupTarget(
                    id = 1L,
                    name = "Read",
                    iconKey = "book",
                    colorKey = "sunrise",
                    reminderEnabled = true,
                    reminderHour = 7,
                    reminderMinute = 30,
                    createdAt = 1234L,
                    sortOrder = 0
                )
            ),
            completions = listOf(
                BackupCompletion(
                    id = 10L,
                    targetId = 1L,
                    dateKey = "2026-05-20",
                    completedAt = 2000L
                )
            )
        )

        val json = BackupCodec.encode(payload)
        val decoded = BackupCodec.decode(json)

        assertEquals(payload, decoded)
    }
}
