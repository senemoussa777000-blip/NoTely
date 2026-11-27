package com.notely.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.notely.app.domain.util.DateUtils
import com.notely.app.ui.components.EmptyState
import com.notely.app.ui.components.NotelyCard

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToNotes: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onCreateNote: () -> Unit,
    onCreateTask: () -> Unit,
    onCreateEvent: () -> Unit,
    onOpenNote: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Bonjour", style = MaterialTheme.typography.headlineMedium)
        Text(
            DateUtils.formatDate(System.currentTimeMillis()),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Résumé : notes et tâches
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NotelyCard(modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                Text("${state.notesCount} notes", style = MaterialTheme.typography.titleMedium)
                Text("Voir mes notes", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                Button(onClick = onNavigateToNotes) { Text("Ouvrir") }
            }
            NotelyCard(modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                Text("${state.remainingTasks} tâches restantes", style = MaterialTheme.typography.titleMedium)
                Text(
                    "${state.totalTasks - state.remainingTasks} / ${state.totalTasks} terminées",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { state.taskProgress },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = onNavigateToTasks) { Text("Ouvrir") }
            }
        }

        // Prochains événements
        Text("Prochains événements", style = MaterialTheme.typography.titleLarge)
        if (state.upcomingEvents.isEmpty()) {
            EmptyState(
                title = "Aucun événement à venir",
                message = "Crée ton premier événement pour l'afficher ici.",
                actionLabel = "Créer un événement",
                onActionClick = onCreateEvent
            )
        } else {
            state.upcomingEvents.forEach { event ->
                NotelyCard(onClick = onNavigateToCalendar) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                        Column {
                            Text(event.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                DateUtils.formatDateTime(event.startTime),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Notes récentes
        Text("Notes récentes", style = MaterialTheme.typography.titleLarge)
        if (state.recentNotes.isEmpty()) {
            EmptyState(
                title = "Aucune note pour l'instant",
                message = "Écris ta première note, elle apparaîtra ici.",
                actionLabel = "Créer une note",
                onActionClick = onCreateNote
            )
        } else {
            state.recentNotes.forEach { note ->
                NotelyCard(
                    backgroundColor = Color(note.color),
                    onClick = { onOpenNote(note.id) }
                ) {
                    Text(
                        note.title.ifBlank { "Sans titre" },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        note.content.take(120),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2
                    )
                }
            }
        }

        // Actions rapides
        Text("Actions rapides", style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onCreateNote) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Text("Note")
            }
            Button(onClick = onCreateTask) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Text("Tâche")
            }
            Button(onClick = onCreateEvent) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Text("Événement")
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}
