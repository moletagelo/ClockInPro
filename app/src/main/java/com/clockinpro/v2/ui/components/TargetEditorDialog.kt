package com.clockinpro.v2.ui.components

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.clockinpro.v2.domain.model.ReminderConfig
import com.clockinpro.v2.domain.model.SaveTargetRequest
import com.clockinpro.v2.domain.model.Target
import com.clockinpro.v2.util.DateKeyUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TargetEditorDialog(
    initialTarget: Target?,
    onDismiss: () -> Unit,
    onConfirm: (SaveTargetRequest) -> Unit
) {
    val context = LocalContext.current
    var name by remember(initialTarget?.id) { mutableStateOf(initialTarget?.name.orEmpty()) }
    var selectedIconKey by remember(initialTarget?.id) {
        mutableStateOf(initialTarget?.iconKey ?: targetIconOptions.first().key)
    }
    var selectedColorKey by remember(initialTarget?.id) {
        mutableStateOf(initialTarget?.colorKey ?: targetColorOptions.first().key)
    }
    var reminderEnabled by remember(initialTarget?.id) {
        mutableStateOf(initialTarget?.reminder?.enabled ?: false)
    }
    var reminderHour by remember(initialTarget?.id) {
        mutableIntStateOf(initialTarget?.reminder?.hour ?: 9)
    }
    var reminderMinute by remember(initialTarget?.id) {
        mutableIntStateOf(initialTarget?.reminder?.minute ?: 0)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (initialTarget == null) "New target" else "Edit target")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Target name") },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    singleLine = true
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Icon",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        targetIconOptions.forEach { option ->
                            FilterChip(
                                selected = selectedIconKey == option.key,
                                onClick = { selectedIconKey = option.key },
                                label = { Text(option.label) },
                                leadingIcon = {
                                    androidx.compose.material3.Icon(
                                        imageVector = option.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Color",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        targetColorOptions.forEach { option ->
                            FilterChip(
                                selected = selectedColorKey == option.key,
                                onClick = { selectedColorKey = option.key },
                                label = { Text(option.label) },
                                leadingIcon = {
                                    ColorSwatch(color = option.color)
                                }
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Daily reminder",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Optional local notification for this target",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { reminderEnabled = it }
                        )
                    }

                    if (reminderEnabled) {
                        OutlinedButton(
                            onClick = {
                                TimePickerDialog(
                                    context,
                                    { _, hourOfDay, minute ->
                                        reminderHour = hourOfDay
                                        reminderMinute = minute
                                    },
                                    reminderHour,
                                    reminderMinute,
                                    true
                                ).show()
                            }
                        ) {
                            Text("Reminder time: ${DateKeyUtils.formatTime(reminderHour, reminderMinute)}")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                    onConfirm(
                        SaveTargetRequest(
                            id = initialTarget?.id,
                            name = name,
                            iconKey = selectedIconKey,
                            colorKey = selectedColorKey,
                            reminder = ReminderConfig(
                                enabled = reminderEnabled,
                                hour = reminderHour,
                                minute = reminderMinute
                            )
                        )
                    )
                }
            ) {
                Text(if (initialTarget == null) "Create" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ColorSwatch(color: Color) {
    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .padding(end = 2.dp)
            .size(16.dp)
    ) {
        drawCircle(color = color)
    }
}
