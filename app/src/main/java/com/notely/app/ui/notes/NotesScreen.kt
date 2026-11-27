package com.notely.app.ui.notes

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
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.notely.app.data.local.entity.Note
import com.notely.app.ui.components.ConfirmDialog
import com.notely.app.ui.components.EmptyState
import com.notely.app.ui.components.NotelyCard
import com.notely.app.ui.components.NotelySearchBar

@Composable
fun NotesScreen(
    viewModel: NotesViewModel,
    onOpenEditor: (Long) -> Unit,
    onCreateNote: () -> Unit
) {
    val notes by viewModel.notes.collectAsState()
    val query by viewModel.query.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val sort by viewModel.sort.collectAsState()
    var noteToDelete by remember { mutableStateOf<Note?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateNote) {
                Icon(Icons.Filled.Add, contentDescription = "Nouvelle note")
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
            Text("Mes notes", style = MaterialTheme.typography.headlineMedium)
            NotelySearchBar(query = query, onQueryChange = viewModel::setQuery)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = filter == NoteFilter.TOUTES,
                    onClick = { viewModel.setFilter(NoteFilter.TOUTES) },
                    label = { Text("Toutes") }
                )
                FilterChip(
                    selected = filter == NoteFilter.FAVORIS,
                    onClick = { viewModel.setFilter(NoteFilter.FAVORIS) },
                    label = { Text("Favoris") }
                )
                FilterChip(
                    selected = filter == NoteFilter.ARCHIVES,
                    onClick = { viewModel.setFilter(NoteFilter.ARCHIVES) },
                    label = { Text("Archives") }
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Trier :", style = MaterialTheme.typography.bodyMedium)
                FilterChip(
                    selected = sort == NoteSort.RECENTES,
                    onClick = { viewModel.setSort(NoteSort.RECENTES) },
                    label = { Text("Récentes") }
                )
                FilterChip(
                    selected = sort == NoteSort.TITRE,
                    onClick = { viewModel.setSort(NoteSort.TITRE) },
                    label = { Text("Titre") }
                )
            }

            if (notes.isEmpty()) {
                EmptyState(
                    title = "Aucune note",
                    message = if (query.isNotBlank()) "Aucune note ne correspond à ta recherche."
                    else "Crée ta première note.",
                    actionLabel = "Créer une note",
                    onActionClick = onCreateNote
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(notes, key = { it.id }) { note ->
                        NotelyCard(
                            backgroundColor = Color(note.color),
                            onClick = { onOpenEditor(note.id) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    note.title.ifBlank { "Sans titre" },
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { viewModel.togglePinned(note) }) {
                                    Icon(
                                        Icons.Filled.PushPin,
                                        contentDescription = "Épingler",
                                        tint = if (note.pinned) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { viewModel.toggleFavorite(note) }) {
                                    Icon(
                                        if (note.favorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                                        contentDescription = "Favori",
                                        tint = if (note.favorite) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                note.content.take(150),
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 3
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(onClick = { viewModel.toggleArchived(note) }) {
                                    Icon(
                                        if (note.archived) Icons.Filled.Unarchive else Icons.Filled.Archive,
                                        contentDescription = if (note.archived) "Désarchiver" else "Archiver"
                                    )
                                }
                                IconButton(onClick = { noteToDelete = note }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Supprimer")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    noteToDelete?.let { note ->
        ConfirmDialog(
            title = "Supprimer cette note ?",
            message = "« ${note.title.ifBlank { "Sans titre" }} » sera supprimée définitivement.",
            onConfirm = { viewModel.delete(note); noteToDelete = null },
            onDismiss = { noteToDelete = null }
        )
    }
}
