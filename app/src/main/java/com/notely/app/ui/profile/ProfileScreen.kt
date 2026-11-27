package com.notely.app.ui.profile

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.notely.app.ui.components.NotelyCard
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val theme by viewModel.theme.collectAsState(initial = "system")
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState(initial = true)
    val language by viewModel.language.collectAsState(initial = "fr")
    val storage by viewModel.storage.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var exportPending by remember { mutableStateOf(false) }
    var exportJson by remember { mutableStateOf<String?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        val json = exportJson
        if (uri != null && json != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(json.toByteArray(Charsets.UTF_8))
                }
                Toast.makeText(context, "Export enregistré.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Export impossible : ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    LaunchedEffect(exportPending) {
        if (exportPending) {
            exportPending = false
            exportJson = viewModel.exportJson()
            exportLauncher.launch("notely-export.json")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Profil et paramètres", style = MaterialTheme.typography.headlineMedium)

        NotelyCard {
            Text("Thème", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("system" to "Système", "clair" to "Clair", "sombre" to "Sombre").forEach { (value, label) ->
                    FilterChip(
                        selected = theme == value,
                        onClick = { viewModel.setTheme(value) },
                        label = { Text(label) }
                    )
                }
            }
        }

        NotelyCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Notifications", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Recevoir les rappels de tâches et d'événements",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { viewModel.setNotificationsEnabled(it) }
                )
            }
        }

        NotelyCard {
            Text("Langue", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                if (language == "fr") "Français" else language,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "D'autres langues seront ajoutées plus tard.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        NotelyCard {
            Text("Stockage", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text("${storage.notes} notes", style = MaterialTheme.typography.bodyMedium)
            Text("${storage.tasks} tâches", style = MaterialTheme.typography.bodyMedium)
            Text("${storage.events} événements", style = MaterialTheme.typography.bodyMedium)
            Text(
                "Toutes les données sont stockées sur ton téléphone, hors ligne.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = { scope.launch { exportPending = true } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Exporter mes données (JSON)")
            }
        }

        NotelyCard {
            Text("À propos de Notely", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text("Tes idées, toujours à portée de main.", style = MaterialTheme.typography.bodyMedium)
            Text("Version 1.0.0", style = MaterialTheme.typography.bodyMedium)
            Text(
                "Notes, tâches et calendrier, dans un seul espace, sans connexion Internet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}
