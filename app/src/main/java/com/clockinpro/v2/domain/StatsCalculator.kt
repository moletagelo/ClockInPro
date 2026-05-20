package com.clockinpro.v2.domain

import com.clockinpro.v2.domain.model.TargetStats
import com.clockinpro.v2.util.DateKeyUtils
import java.time.LocalDate

object StatsCalculator {
    fun calculate(
        completionDateKeys: List<String>,
        today: LocalDate = DateKeyUtils.today()
    ): TargetStats {
        val dates = completionDateKeys.map(DateKeyUtils::parseDateKey).toSet()
        var streak = 0
        var cursor = today

        while (dates.contains(cursor)) {
            streak += 1
            cursor = cursor.minusDays(1)
        }

        return TargetStats(
            currentStreak = streak,
            totalCompletions = dates.size
        )
    }
}
