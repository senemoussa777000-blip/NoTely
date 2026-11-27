package com.notely.app.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.notely.app.data.local.entity.Event
import com.notely.app.domain.util.DateUtils
import com.notely.app.ui.components.ConfirmDialog
import com.notely.app.ui.components.EmptyState
import com.notely.app.ui.components.NotelyCard
import java.util.Calendar

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onOpenEventEditor: (eventId: Long, dateMillis: Long) -> Unit
) {
    val month by viewModel.currentMonth.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()
    val dayEvents by viewModel.dayEvents.collectAsState()
    val monthEventDays by viewModel.monthEventDays.collectAsState()
    var eventToDelete by remember { mutableStateOf<Event?>(null) }

    val cal = Calendar.getInstance().apply { timeInMillis = month }
    val year = cal.get(Calendar.YEAR)
    val monthIndex = cal.get(Calendar.MONTH)
    val daysInMonth = DateUtils.daysInMonth(month)
    val offset = DateUtils.firstDayOffset(month)
    val cells: List<Int?> = List(offset) { null } + (1..daysInMonth).toList()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onOpenEventEditor(-1L, selectedDay) }) {
                Icon(Icons.Filled.Add, contentDescription = "Nouvel événement")
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
            Text("Calendrier", style = MaterialTheme.typography.headlineMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = viewModel::previousMonth) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Mois précédent")
                }
                Text(DateUtils.monthTitle(month), style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = viewModel::nextMonth) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Mois suivant")
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim").forEach { day ->
                    Text(
                        day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(cells) { day ->
                    if (day == null) {
                        Box(modifier = Modifier.aspectRatio(1f))
                    } else {
                        val dayMillis = DateUtils.millisForDay(year, monthIndex, day)
                        val isSelected = dayMillis == selectedDay
                        val isToday = DateUtils.isToday(dayMillis)
                        val hasEvent = dayMillis in monthEventDays
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        isToday -> MaterialTheme.colorScheme.secondary
                                        else -> MaterialTheme.colorScheme.surface
                                    }
                                )
                                .clickable { viewModel.selectDay(dayMillis) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    day.toString(),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (hasEvent) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.onPrimary
                                                else MaterialTheme.colorScheme.primary
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Text(
                "Événements du ${DateUtils.formatShortDate(selectedDay)}",
                style = MaterialTheme.typography.titleLarge
            )
            if (dayEvents.isEmpty()) {
                EmptyState(
                    title = "Aucun événement ce jour-là",
                    message = "Crée un événement pour ce jour.",
                    actionLabel = "Créer un événement",
                    onActionClick = { onOpenEventEditor(-1L, selectedDay) }
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(dayEvents, key = { it.id }) { event ->
                        NotelyCard {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(event.title, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "${DateUtils.formatTime(event.startTime)} – ${DateUtils.formatTime(event.endTime)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (event.description.isNotBlank()) {
                                        Text(event.description, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                                TextButton(onClick = {
                                    onOpenEventEditor(event.id, event.startTime)
                                }) { Text("Modifier") }
                                IconButton(onClick = { eventToDelete = event }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Supprimer")
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    eventToDelete?.let { event ->
        ConfirmDialog(
            title = "Supprimer cet événement ?",
            message = "« ${event.title} » sera supprimé définitivement.",
            onConfirm = { viewModel.deleteEvent(event); eventToDelete = null },
            onDismiss = { eventToDelete = null }
        )
    }
}
