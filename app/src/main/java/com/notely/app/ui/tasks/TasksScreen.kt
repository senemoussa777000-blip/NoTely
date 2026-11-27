package com.notely.app.ui.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.notely.app.data.local.entity.Task
import com.notely.app.domain.util.DateUtils
import com.notely.app.domain.util.Priority
import com.notely.app.ui.components.ConfirmDialog
import com.notely.app.ui.components.EmptyState
import com.notely.app.ui.components.NotelyCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(viewModel: TasksViewModel) {
    val state by viewModel.uiState.collectAsState()
    val filter by viewModel.filter.collectAsState()
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var creating by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { creating = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Nouvelle tâche")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Mes tâches", style = MaterialTheme.typography.headlineMedium)
            Text(
                "${state.done} / ${state.total} terminées",
                style = MaterialTheme.typography.bodyMedium
            )
            LinearProgressIndicator(
                progress = { state.progress },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskFilter.entries.forEach { f ->
                    FilterChip(
                        selected = filter == f,
                        onClick = { viewModel.setFilter(f) },
                        label = {
                            Text(
                                when (f) {
                                    TaskFilter.TOUTES -> "Toutes"
                                    TaskFilter.AUJOURDUI -> "Aujourd'hui"
                                    TaskFilter.A_VENIR -> "À venir"
                                    TaskFilter.TERMINEES -> "Terminées"
                                }
                            )
                        }
                    )
                }
            }

            if (state.tasks.isEmpty()) {
                EmptyState(
                    title = "Aucune tâche ici",
                    message = "Crée ta première tâche pour commencer.",
                    actionLabel = "Créer une tâche",
                    onActionClick = { creating = true }
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.tasks, key = { it.id }) { task ->
                        NotelyCard {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = task.done,
                                    onCheckedChange = { viewModel.toggleDone(task) }
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        task.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        textDecoration = if (task.done) TextDecoration.LineThrough else null
                                    )
                                    if (task.description.isNotBlank()) {
                                        Text(task.description, style = MaterialTheme.typography.bodyMedium)
                                    }
                                    val details = buildList {
                                        add(Priority.fromValue(task.priority).label)
                                        task.dueDate?.let { add(DateUtils.formatShortDate(it)) }
                                        if (task.dueDate != null && DateUtils.isOverdue(task.dueDate) && !task.done) {
                                            add("En retard")
                                        }
                                    }.joinToString(" · ")
                                    Text(
                                        details,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                TextButton(onClick = { editingTask = task }) { Text("Modifier") }
                                IconButton(onClick = { taskToDelete = task }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Supprimer")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (creating) {
        TaskDialog(
            task = null,
            onSave = { task ->
                viewModel.saveTask(task)
                creating = false
            },
            onDismiss = { creating = false }
        )
    }
    editingTask?.let { task ->
        TaskDialog(
            task = task,
            onSave = { updated ->
                viewModel.saveTask(updated)
                editingTask = null
            },
            onDismiss = { editingTask = null }
        )
    }
    taskToDelete?.let { task ->
        ConfirmDialog(
            title = "Supprimer cette tâche ?",
            message = "« ${task.title} » sera supprimée définitivement.",
            onConfirm = { viewModel.delete(task); taskToDelete = null },
            onDismiss = { taskToDelete = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDialog(
    task: Task?,
    onSave: (Task) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var priority by remember { mutableStateOf(Priority.fromValue(task?.priority ?: "moyenne")) }
    var dueDate by remember { mutableStateOf(task?.dueDate) }
    var reminder by remember { mutableStateOf(task?.reminderAt != null) }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task == null) "Nouvelle tâche" else "Modifier la tâche") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                Text("Priorité", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Priority.entries.forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p.label) }
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        dueDate?.let { "Pour le ${DateUtils.formatShortDate(it)}" } ?: "Sans date limite",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(onClick = { showDatePicker = true }) {
                        Text(if (dueDate == null) "Choisir une date" else "Changer")
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Me rappeler (1 h avant)", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = reminder,
                        onCheckedChange = { reminder = it },
                        enabled = dueDate != null
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank(),
                onClick = {
                    val reminderAt = if (reminder && dueDate != null) {
                        dueDate!! - 3_600_000L
                    } else null
                    onSave(
                        (task ?: Task(title = title)).copy(
                            title = title,
                            description = description,
                            priority = priority.value,
                            dueDate = dueDate,
                            reminderAt = reminderAt
                        )
                    )
                }
            ) { Text("Enregistrer") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )

    if (showDatePicker) {
        val dateState = rememberDatePickerState(
            initialSelectedDateMillis = dueDate ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let { dueDate = it }
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
}
