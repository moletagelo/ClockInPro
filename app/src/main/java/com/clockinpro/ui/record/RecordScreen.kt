package com.clockinpro.ui.record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.clockinpro.domain.model.CheckRecord
import com.clockinpro.domain.model.CheckType
import com.clockinpro.ui.theme.Secondary
import com.clockinpro.ui.theme.Success
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    viewModel: RecordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("打卡记录") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    CalendarCard(
                        selectedDate = uiState.selectedDate,
                        checkedDates = uiState.checkedDates,
                        onDateSelected = { viewModel.selectDate(it) },
                        onMonthChanged = { viewModel.changeMonth(it) }
                    )
                }

                item {
                    StatsCard(stats = uiState.monthStats)
                }

                item {
                    Text(
                        text = "打卡详情",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (uiState.records.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.History,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "暂无打卡记录",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(uiState.records) { record ->
                        RecordItem(record = record)
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarCard(
    selectedDate: Long,
    checkedDates: Set<String>,
    onDateSelected: (Long) -> Unit,
    onMonthChanged: (Int) -> Unit
) {
    val calendar = remember { Calendar.getInstance() }
    calendar.timeInMillis = selectedDate

    val year = remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    val month = remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }

    val monthFormat = remember { SimpleDateFormat("yyyy年MM月", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    calendar.set(year.value, month.value, 1)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    val selectedDateFormat = remember(selectedDate) {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(selectedDate))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    month.value -= 1
                    if (month.value < 0) {
                        month.value = 11
                        year.value -= 1
                    }
                    calendar.set(year.value, month.value, 1)
                    onDateSelected(calendar.timeInMillis)
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "上个月")
                }

                Text(
                    text = monthFormat.format(calendar.time),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = {
                    month.value += 1
                    if (month.value > 11) {
                        month.value = 0
                        year.value += 1
                    }
                    calendar.set(year.value, month.value, 1)
                    onDateSelected(calendar.timeInMillis)
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "下个月")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("日", "一", "二", "三", "四", "五", "六").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val totalCells = firstDayOfWeek + daysInMonth
            val rows = (totalCells + 6) / 7

            Column {
                for (row in 0 until rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (col in 0..6) {
                            val dayIndex = row * 7 + col - firstDayOfWeek + 1
                            if (dayIndex in 1..daysInMonth) {
                                calendar.set(year.value, month.value, dayIndex)
                                val dateStr = dateFormat.format(calendar.time)
                                val isSelected = dateStr == selectedDateFormat
                                val isChecked = checkedDates.contains(dateStr)
                                val isToday = dateStr == dateFormat.format(Date())

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSelected -> MaterialTheme.colorScheme.primary
                                                isChecked -> MaterialTheme.colorScheme.primaryContainer
                                                else -> MaterialTheme.colorScheme.surface
                                            }
                                        )
                                        .clickable {
                                            calendar.set(year.value, month.value, dayIndex)
                                            onDateSelected(calendar.timeInMillis)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = dayIndex.toString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = when {
                                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                                isToday -> MaterialTheme.colorScheme.primary
                                                else -> MaterialTheme.colorScheme.onSurface
                                            },
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (isChecked && !isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .background(
                                                        Success,
                                                        CircleShape
                                                    )
                                            )
                                        }
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsCard(stats: MonthStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                label = "本月打卡",
                value = "${stats.checkInDays}",
                unit = "天"
            )
            StatItem(
                label = "打卡次数",
                value = "${stats.totalCheckIns}",
                unit = "次"
            )
            StatItem(
                label = "出勤率",
                value = if (stats.totalDays > 0) "${(stats.checkInDays * 100 / stats.totalDays)}" else "0",
                unit = "%"
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    unit: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
            )
        }
    }
}

@Composable
private fun RecordItem(record: CheckRecord) {
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (record.type == CheckType.CHECK_IN)
                            MaterialTheme.colorScheme.primaryContainer
                        else Secondary.copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (record.type == CheckType.CHECK_IN)
                        Icons.Default.Login else Icons.Default.Logout,
                    contentDescription = null,
                    tint = if (record.type == CheckType.CHECK_IN)
                        MaterialTheme.colorScheme.primary else Secondary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (record.type == CheckType.CHECK_IN) "签到" else "签退",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                record.address?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                record.remark?.let {
                    Text(
                        text = "备注: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = timeFormat.format(Date(record.timestamp)),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = when (record.syncStatus) {
                        com.clockinpro.domain.model.SyncStatus.SYNCED -> "已同步"
                        com.clockinpro.domain.model.SyncStatus.PENDING -> "待同步"
                        com.clockinpro.domain.model.SyncStatus.FAILED -> "同步失败"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
