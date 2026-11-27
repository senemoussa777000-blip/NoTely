package com.notely.app.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Travail unique planifié par WorkManager : affiche une notification
 * à l'heure du rappel d'une tâche ou d'un événement.
 * À tester sur un vrai téléphone : les rappels ne sont pas vérifiables ici.
 */
class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE) ?: return Result.success()
        val text = inputData.getString(KEY_TEXT) ?: title
        NotificationHelper.showNotification(
            applicationContext,
            Random.nextInt(),
            title,
            text
        )
        return Result.success()
    }

    companion object {
        const val KEY_TITLE = "title"
        const val KEY_TEXT = "text"

        fun schedule(
            context: Context,
            workName: String,
            title: String,
            text: String,
            triggerAtMillis: Long
        ) {
            val delay = triggerAtMillis - System.currentTimeMillis()
            if (delay <= 0) return
            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf(KEY_TITLE to title, KEY_TEXT to text))
                .addTag(workName)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(workName, ExistingWorkPolicy.REPLACE, request)
        }

        fun cancel(context: Context, workName: String) {
            WorkManager.getInstance(context).cancelUniqueWork(workName)
        }
    }
}
