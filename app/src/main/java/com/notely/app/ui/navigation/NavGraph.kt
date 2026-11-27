package com.notely.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.notely.app.data.repository.EventRepository
import com.notely.app.data.repository.NoteRepository
import com.notely.app.data.repository.SettingsRepository
import com.notely.app.data.repository.TaskRepository
import com.notely.app.ui.calendar.CalendarScreen
import com.notely.app.ui.calendar.CalendarViewModel
import com.notely.app.ui.calendar.EventEditorScreen
import com.notely.app.ui.home.HomeScreen
import com.notely.app.ui.home.HomeViewModel
import com.notely.app.ui.notes.NoteEditorScreen
import com.notely.app.ui.notes.NoteEditorViewModel
import com.notely.app.ui.notes.NotesScreen
import com.notely.app.ui.notes.NotesViewModel
import com.notely.app.ui.profile.ProfileScreen
import com.notely.app.ui.profile.ProfileViewModel
import com.notely.app.ui.tasks.TasksScreen
import com.notely.app.ui.tasks.TasksViewModel

/**
 * Graphe de navigation. Les repositories sont fournis par MainActivity
 * (depuis NotelyApp) et chaque écran reçoit son ViewModel via une factory.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    noteRepository: NoteRepository,
    taskRepository: TaskRepository,
    eventRepository: EventRepository,
    settingsRepository: SettingsRepository
) {
    NavHost(navController = navController, startDestination = Destinations.HOME) {

        composable(Destinations.HOME) {
            val viewModel: HomeViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { HomeViewModel(noteRepository, taskRepository, eventRepository) }
                }
            )
            HomeScreen(
                viewModel = viewModel,
                onNavigateToNotes = { navController.navigate(Destinations.NOTES) },
                onNavigateToTasks = { navController.navigate(Destinations.TASKS) },
                onNavigateToCalendar = { navController.navigate(Destinations.CALENDAR) },
                onCreateNote = { navController.navigate(Destinations.noteEditor()) },
                onCreateTask = { navController.navigate(Destinations.TASKS) },
                onCreateEvent = { navController.navigate(Destinations.eventEditor()) },
                onOpenNote = { noteId -> navController.navigate(Destinations.noteEditor(noteId)) }
            )
        }

        composable(Destinations.NOTES) {
            val viewModel: NotesViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { NotesViewModel(noteRepository) }
                }
            )
            NotesScreen(
                viewModel = viewModel,
                onOpenEditor = { noteId -> navController.navigate(Destinations.noteEditor(noteId)) },
                onCreateNote = { navController.navigate(Destinations.noteEditor()) }
            )
        }

        composable(
            route = Destinations.NOTE_EDITOR,
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
            val viewModel: NoteEditorViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { NoteEditorViewModel(noteRepository) }
                }
            )
            NoteEditorScreen(
                viewModel = viewModel,
                noteId = noteId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Destinations.TASKS) {
            val viewModel: TasksViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { TasksViewModel(taskRepository) }
                }
            )
            TasksScreen(viewModel = viewModel)
        }

        composable(Destinations.CALENDAR) {
            val viewModel: CalendarViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { CalendarViewModel(eventRepository) }
                }
            )
            CalendarScreen(
                viewModel = viewModel,
                onOpenEventEditor = { eventId, dateMillis ->
                    navController.navigate(Destinations.eventEditor(eventId, dateMillis))
                }
            )
        }

        composable(
            route = Destinations.EVENT_EDITOR,
            arguments = listOf(
                navArgument("eventId") { type = NavType.LongType },
                navArgument("dateMillis") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getLong("eventId") ?: -1L
            val dateMillis = backStackEntry.arguments?.getLong("dateMillis")
                ?: System.currentTimeMillis()
            val viewModel: CalendarViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { CalendarViewModel(eventRepository) }
                }
            )
            EventEditorScreen(
                viewModel = viewModel,
                eventId = eventId,
                dateMillis = dateMillis,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Destinations.PROFILE) {
            val viewModel: ProfileViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        ProfileViewModel(
                            settingsRepository,
                            noteRepository,
                            taskRepository,
                            eventRepository
                        )
                    }
                }
            )
            ProfileScreen(viewModel = viewModel)
        }
    }
}
