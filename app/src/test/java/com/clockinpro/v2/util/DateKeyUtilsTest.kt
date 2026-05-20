package com.clockinpro.v2.util

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class DateKeyUtilsTest {

    @Test
    fun `formats date keys with zero padded month and day`() {
        assertEquals("2026-05-02", DateKeyUtils.toDateKey(LocalDate.of(2026, 5, 2)))
    }

    @Test
    fun `parses date keys back into local dates`() {
        assertEquals(LocalDate.of(2026, 12, 31), DateKeyUtils.parseDateKey("2026-12-31"))
    }
}
