package com.clockinpro.v2.domain

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class StatsCalculatorTest {

    @Test
    fun `counts streak ending today across consecutive completions`() {
        val result = StatsCalculator.calculate(
            completionDateKeys = listOf("2026-05-18", "2026-05-19", "2026-05-20"),
            today = LocalDate.of(2026, 5, 20)
        )

        assertEquals(3, result.currentStreak)
        assertEquals(3, result.totalCompletions)
    }

    @Test
    fun `resets streak when today is missing`() {
        val result = StatsCalculator.calculate(
            completionDateKeys = listOf("2026-05-18", "2026-05-19"),
            today = LocalDate.of(2026, 5, 20)
        )

        assertEquals(0, result.currentStreak)
        assertEquals(2, result.totalCompletions)
    }
}
