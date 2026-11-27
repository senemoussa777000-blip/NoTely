package com.notely.app.domain.util

/** Priorité d'une tâche, avec son libellé français. */
enum class Priority(val value: String, val label: String) {
    BASSE("basse", "Basse"),
    MOYENNE("moyenne", "Moyenne"),
    HAUTE("haute", "Haute");

    companion object {
        fun fromValue(value: String): Priority =
            entries.firstOrNull { it.value == value } ?: MOYENNE
    }
}
