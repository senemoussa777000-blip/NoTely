package com.notely.app.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notely.app.data.local.entity.Event
import com.notely.app.domain.util.DateUtils
import com.notely.app.ui.components.ConfirmDialog
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEditorScreen(
    viewModel: CalendarViewModel,
    eventId: Long,
    dateMillis: Long,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(DateUtils.startOfDay(dateMillis)) }
    var startHour by remember { mutableStateOf(9) }
    var startMinute by remember { mutableStateOf(0) }
    var endHour by remember { mutableStateOf(10) }
    var endMinute by remember { mutableStateOf(0) }
    var reminder by remember { mutableStateOf(false) }
    var initialized by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var editingTime by remember { mutableStateOf<String?>(null) } // "start" ou "end"
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(eventId) {
        if (eventId != -1L && !initialized) {
            viewModel.getEvent(eventId)?.let { event ->
                title = event.title
                description = event.description
                date = DateUtils.startOfDay(event.startTime)
                Calendar.getInstance().apply { timeInMillis = event.startTime }.let {
                    startHour = it.get(Calendar.HOUR_OF_DAY)
                    startMinute = it.get(Calendar.MINUTE)
                }
                Calendar.getInstance().apply { timeInMillis = event.endTime }.let {
                    endHour = it.get(Calendar.HOUR_OF_DAY)
                    endMinute = it.get(Calendar.MINUTE)
                }
                reminder = event.reminderAt != null
                initialized = true
            }
        } else {
            initialized = true
        }
    }

    fun withTime(dayMillis: Long, hour: Int, minute: Int): Long =
        Calendar.getInstance().apply {
            timeInMillis = dayMillis
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
            }
            Text(
                if (eventId == -1L) "Nouvel événement" else "Modifier l'événement",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            if (eventId != -1L) {
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Supprimer")
                }
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Titre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description (facultatif)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(DateUtils.formatShortDate(date), style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = { showDatePicker = true }) { Text("Changer la date") }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Début : ${"%02d:%02d".format(startHour, startMinute)}",
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(onClick = { editingTime = "start" }) { Text("Changer") }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Fin : ${"%02d:%02d".format(endHour, endMinute)}",
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(onClick = { editingTime = "end" }) { Text("Changer") }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Me rappeler (15 min avant)", style = MaterialTheme.typography.bodyMedium)
            Switch(checked = reminder, onCheckedChange = { reminder = it })
        }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                var start = withTime(date, startHour, startMinute)
                var end = withTime(date, endHour, endMinute)
                if (end <= start) end = start + 3_600_000L
                val event = Event(
                    id = if (eventId == -1L) 0L else eventId,
                    title = title,
                    description = description,
                    startTime = start,
                    endTime = end,
                    reminderAt = if (reminder) start - 900_000L else null
                )
                viewModel.saveEvent(event)
                onBack()
            },
            enabled = title.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enregistrer")
        }
    }

    if (showDatePicker) {
        val dateState = rememberDatePickerState(initialSelectedDateMillis = date)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let { date = DateUtils.startOfDay(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Annuler") }
            }
        ) {
            DatePicker(state = dateState)
        }
    }

    editingTime?.let { which ->
        val timeState = rememberTimePickerState(
            initialHour = if (which == "start") startHour else endHour,
            initialMinute = if (which == "start") startMinute else endMinute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { editingTime = null },
            title = { Text(if (which == "start") "Heure de début" else "Heure de fin") },
            text = { TimePicker(state = timeState) },
            confirmButton = {
                TextButton(onClick = {
                    if (which == "start") {
                        startHour = timeState.hour
                        startMinute = timeState.minute
                    } else {
                        endHour = timeState.hour
                        endMinute = timeState.minute
                    }
                    editingTime = null
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { editingTime = null }) { Text("Annuler") }
            }
        )
    }

    if (showDeleteDialog) {
        ConfirmDialog(
            title = "Supprimer cet événement ?",
            message = "Cet événement sera supprimé définitivement.",
            onConfirm = {
                viewModel.deleteEvent(
                    Event(id = eventId, title = title, startTime = date, endTime = date)
                )
                onBack()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}
