package com.clockinpro.v2.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.clockinpro.v2.domain.model.Target
import com.clockinpro.v2.domain.model.TargetSummary
import com.clockinpro.v2.ui.components.TargetEditorDialog
import com.clockinpro.v2.ui.components.colorForKey
import com.clockinpro.v2.ui.components.iconForKey
import com.clockinpro.v2.util.DateKeyUtils
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
    onOpenSettings: () -> Unit,
    onOpenTarget: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }
    var editingTarget by remember { mutableStateOf<Target?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Target?>(null) }

    if (showCreateDialog || editingTarget != null) {
        TargetEditorDialog(
            initialTarget = editingTarget,
            onDismiss = {
                showCreateDialog = false
                editingTarget = null
            },
            onConfirm = { request ->
                scope.launch {
                    viewModel.saveTarget(request)
                    showCreateDialog = false
                    editingTarget = null
                }
            }
        )
    }

    deleteTarget?.let { target ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete target?") },
            text = { Text("This removes ${target.name} and all of its check-in history.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            viewModel.deleteTarget(target.id)
                            deleteTarget = null
                        }
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    HomeScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onOpenSettings = onOpenSettings,
        onAddTarget = { showCreateDialog = true },
        onEditTarget = { editingTarget = it },
        onDeleteTarget = { deleteTarget = it },
        onOpenTarget = onOpenTarget,
        onCompleteTarget = { summary ->
            scope.launch {
                val completed = viewModel.completeTarget(summary.target.id)
                if (completed) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    val result = snackbarHostState.showSnackbar(
                        message = "${summary.target.name} marked complete",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )
                    if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                        viewModel.undoCompleteTarget(summary.target.id)
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    uiState: HomeUiState,
    snackbarHostState: SnackbarHostState,
    onOpenSettings: () -> Unit,
    onAddTarget: () -> Unit,
    onEditTarget: (Target) -> Unit,
    onDeleteTarget: (Target) -> Unit,
    onOpenTarget: (Long) -> Unit,
    onCompleteTarget: (TargetSummary) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("ClockInPro")
                        Text(
                            text = uiState.todayLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTarget) {
                Icon(Icons.Default.Add, contentDescription = "Add target")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        if (uiState.targets.isEmpty()) {
            EmptyHomeState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onAddTarget = onAddTarget
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(168.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.targets, key = { it.target.id }) { summary ->
                    TargetCard(
                        summary = summary,
                        onOpen = { onOpenTarget(summary.target.id) },
                        onEdit = { onEditTarget(summary.target) },
                        onDelete = { onDeleteTarget(summary.target) },
                        onComplete = { onCompleteTarget(summary) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyHomeState(
    modifier: Modifier = Modifier,
    onAddTarget: () -> Unit
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Your dashboard is ready",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Create your first target and start tracking with one tap.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onAddTarget) {
                    Text("Create first target")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TargetCard(
    summary: TargetSummary,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onComplete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val accent = colorForKey(summary.target.colorKey)
    val containerColor by animateColorAsState(
        if (summary.isCompletedToday) accent.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface,
        label = "targetCardColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onOpen),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = accent.copy(alpha = 0.16f),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = iconForKey(summary.target.iconKey),
                            contentDescription = null,
                            tint = accent
                        )
                    }
                }
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            menuExpanded = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        }
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = summary.target.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (summary.target.reminder.enabled) {
                        "Reminder ${DateKeyUtils.formatTime(summary.target.reminder.hour, summary.target.reminder.minute)}"
                    } else {
                        "No reminder"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(
                onClick = onComplete,
                enabled = !summary.isCompletedToday,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (summary.isCompletedToday) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = null
                )
                Text(
                    text = if (summary.isCompletedToday) "Completed today" else "Tap to complete",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            AnimatedVisibility(visible = summary.isCompletedToday) {
                Text(
                    text = "Locked in for today",
                    style = MaterialTheme.typography.bodySmall,
                    color = accent,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
