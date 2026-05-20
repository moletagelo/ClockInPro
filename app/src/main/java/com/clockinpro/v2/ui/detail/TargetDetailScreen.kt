package com.clockinpro.v2.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.clockinpro.v2.domain.model.Target
import com.clockinpro.v2.ui.components.TargetEditorDialog
import com.clockinpro.v2.ui.components.colorForKey
import com.clockinpro.v2.ui.components.iconForKey
import com.clockinpro.v2.util.DateKeyUtils
import java.time.LocalDate
import kotlinx.coroutines.launch

@Composable
fun TargetDetailRoute(
    onBack: () -> Unit,
    viewModel: TargetDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var editingTarget by remember { mutableStateOf<Target?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    uiState.detail?.target?.let { target ->
        if (editingTarget != null) {
            TargetEditorDialog(
                initialTarget = editingTarget,
                onDismiss = { editingTarget = null },
                onConfirm = { request ->
                    scope.launch {
                        viewModel.saveTarget(request)
                        editingTarget = null
                    }
                }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete target?") },
                text = { Text("This removes ${target.name} and every saved completion for it.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                viewModel.deleteTarget()
                                showDeleteDialog = false
                                onBack()
                            }
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

    TargetDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onEdit = { editingTarget = it },
        onDelete = { showDeleteDialog = true },
        onPreviousMonth = viewModel::showPreviousMonth,
        onNextMonth = viewModel::showNextMonth
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TargetDetailScreen(
    uiState: TargetDetailUiState,
    onBack: () -> Unit,
    onEdit: (Target) -> Unit,
    onDelete: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val detail = uiState.detail

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(detail?.target?.name ?: "Target") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (detail != null) {
                        IconButton(onClick = { onEdit(detail.target) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (detail == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Target not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IdentityCard(target = detail.target)
                StatsCardRow(
                    streak = detail.stats.currentStreak,
                    total = detail.stats.totalCompletions
                )
                CalendarCard(
                    monthLabel = uiState.monthLabel,
                    days = uiState.calendarDays,
                    onPreviousMonth = onPreviousMonth,
                    onNextMonth = onNextMonth
                )
                RecentCompletionsCard(completionDates = detail.completions.map { it.date })
            }
        }
    }
}

@Composable
private fun IdentityCard(target: Target) {
    val accent = colorForKey(target.colorKey)
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconForKey(target.iconKey),
                    contentDescription = null,
                    tint = accent
                )
            }
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = target.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (target.reminder.enabled) {
                        "Reminder ${DateKeyUtils.formatTime(target.reminder.hour, target.reminder.minute)}"
                    } else {
                        "No reminder configured"
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatsCardRow(
    streak: Int,
    total: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatTile(
            modifier = Modifier.weight(1f),
            label = "Current streak",
            value = streak.toString()
        )
        StatTile(
            modifier = Modifier.weight(1f),
            label = "Total completions",
            value = total.toString()
        )
    }
}

@Composable
private fun StatTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CalendarCard(
    monthLabel: String,
    days: List<CalendarDayUiState>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val weekdayLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month")
                }
                Text(
                    text = monthLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = onNextMonth) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next month")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekdayLabels.forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            days.chunked(7).forEach { week ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    week.forEach { day ->
                        CalendarCell(day = day, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarCell(
    day: CalendarDayUiState,
    modifier: Modifier = Modifier
) {
    val highlightColor = when {
        day.isCompleted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
        day.isToday -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surface
    }
    val textColor = when {
        day.isCompleted -> MaterialTheme.colorScheme.primary
        day.isToday -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier.padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = CircleShape,
            color = if (day.date != null) highlightColor else MaterialTheme.colorScheme.surface
        ) {
            Box(
                modifier = Modifier.size(36.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.label,
                    color = if (day.date != null) textColor else MaterialTheme.colorScheme.surface,
                    fontWeight = if (day.isCompleted || day.isToday) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun RecentCompletionsCard(
    completionDates: List<LocalDate>
) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Recent completions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (completionDates.isEmpty()) {
                Text(
                    text = "No check-ins yet",
                    modifier = Modifier.padding(top = 12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                completionDates.sortedDescending().take(8).forEach { date ->
                    Text(
                        text = DateKeyUtils.formatDate(date),
                        modifier = Modifier.padding(top = 10.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
