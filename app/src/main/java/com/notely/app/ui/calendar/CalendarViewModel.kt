package com.notely.app.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notely.app.data.local.entity.Event
import com.notely.app.data.repository.EventRepository
import com.notely.app.domain.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class CalendarViewModel(private val repository: EventRepository) : ViewModel() {

    /** Premier jour du mois affiché (à minuit). */
    val currentMonth = MutableStateFlow(
        DateUtils.startOfDay(System.currentTimeMillis()).let { today ->
            val cal = Calendar.getInstance().apply {
                timeInMillis = today
                set(Calendar.DAY_OF_MONTH, 1)
            }
            cal.timeInMillis
        }
    )

    /** Jour sélectionné (à minuit). */
    val selectedDay = MutableStateFlow(DateUtils.startOfDay(System.currentTimeMillis()))

    /** Événements du jour sélectionné. */
    val dayEvents: StateFlow<List<Event>> = selectedDay.flatMapLatest { day ->
        repository.getEventsBetween(DateUtils.startOfDay(day), DateUtils.endOfDay(day))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Jours du mois affiché qui ont au moins un événement (à minuit). */
    val monthEventDays: StateFlow<Set<Long>> = currentMonth.flatMapLatest { month ->
        val cal = Calendar.getInstance().apply { timeInMillis = month }
        val start = cal.timeInMillis
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        val end = DateUtils.endOfDay(cal.timeInMillis)
        repository.getEventsBetween(start, end)
    }.map { events ->
        events.map { DateUtils.startOfDay(it.startTime) }.toSet()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun previousMonth() {
        currentMonth.value = DateUtils.addMonths(currentMonth.value, -1)
    }

    fun nextMonth() {
        currentMonth.value = DateUtils.addMonths(currentMonth.value, 1)
    }

    fun selectDay(dayMillis: Long) {
        selectedDay.value = DateUtils.startOfDay(dayMillis)
        val cal = Calendar.getInstance().apply { timeInMillis = dayMillis }
        val monthStart = Calendar.getInstance().apply {
            timeInMillis = dayMillis
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        if (cal.get(Calendar.YEAR) != Calendar.getInstance().apply { timeInMillis = currentMonth.value }.get(Calendar.YEAR) ||
            cal.get(Calendar.MONTH) != Calendar.getInstance().apply { timeInMillis = currentMonth.value }.get(Calendar.MONTH)
        ) {
            currentMonth.value = monthStart
        }
    }

    suspend fun getEvent(id: Long): Event? = repository.getById(id)

    fun saveEvent(event: Event) = viewModelScope.launch { repository.save(event) }

    fun deleteEvent(event: Event) = viewModelScope.launch { repository.delete(event) }
}
