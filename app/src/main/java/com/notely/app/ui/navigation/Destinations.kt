package com.notely.app.ui.navigation

/** Routes de la navigation inférieure et des écrans d'édition. */
object Destinations {
    const val HOME = "home"
    const val NOTES = "notes"
    const val TASKS = "tasks"
    const val CALENDAR = "calendar"
    const val PROFILE = "profile"

    const val NOTE_EDITOR = "note_editor/{noteId}"
    const val EVENT_EDITOR = "event_editor/{eventId}/{dateMillis}"

    fun noteEditor(noteId: Long = -1L): String = "note_editor/$noteId"

    fun eventEditor(eventId: Long = -1L, dateMillis: Long = System.currentTimeMillis()): String =
        "event_editor/$eventId/$dateMillis"

    /** Les 5 onglets de la barre de navigation inférieure, dans l'ordre. */
    val bottomTabs: List<String> = listOf(HOME, NOTES, TASKS, CALENDAR, PROFILE)
}
