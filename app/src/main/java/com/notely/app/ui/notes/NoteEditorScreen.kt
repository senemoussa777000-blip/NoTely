package com.notely.app.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.notely.app.ui.components.ConfirmDialog
import com.notely.app.ui.theme.NoteColors

@Composable
fun NoteEditorScreen(
    viewModel: NoteEditorViewModel,
    noteId: Long,
    onBack: () -> Unit
) {
    val note by viewModel.note.collectAsState()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var color by remember { mutableStateOf(NoteColors.first()) }
    var initialized by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) { viewModel.load(noteId) }

    LaunchedEffect(note) {
        val current = note
        if (current != null && !initialized) {
            title = current.title
            content = current.content
            color = current.color
            initialized = true
        }
    }

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
                if (noteId == -1L) "Nouvelle note" else "Modifier la note",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            if (noteId != -1L) {
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
            value = content,
            onValueChange = { content = it },
            label = { Text("Contenu") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Text("Couleur", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NoteColors.forEach { noteColor ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(noteColor))
                        .then(
                            if (color == noteColor) {
                                Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            } else Modifier
                        )
                        .clickable { color = noteColor }
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { viewModel.save(title, content, color); onBack() },
            enabled = title.isNotBlank() || content.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enregistrer")
        }
    }

    if (showDeleteDialog) {
        ConfirmDialog(
            title = "Supprimer cette note ?",
            message = "Cette note sera supprimée définitivement.",
            onConfirm = { viewModel.delete(); onBack() },
            onDismiss = { showDeleteDialog = false }
        )
    }
}
