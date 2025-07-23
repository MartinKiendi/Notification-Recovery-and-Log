package com.example.servicesandroid

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.servicesandroid.room.Notification
import com.example.servicesandroid.room.NotificationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.serialization.json.Json


@HiltWorker
class NotificationWorker @AssistedInject constructor(
    val notificationRepository: NotificationRepository,
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val jsonString = inputData.getString("notification")

        if (jsonString.isNullOrEmpty()) return Result.failure()

        return try {
            val notification = Json.decodeFromString<Notification>(jsonString)
            Log.e("ServicessNotificationEntity", "NotificationEntityDecoded Before Input: ${notification.title}")
            if (notification.title.isBlank() || notification.text.isBlank()){
                return Result.failure()
            }else{
                notificationRepository.insertNotification(notification)
                Log.e("ServicessNotificationEntity", "NotificationEntityDecoded After Input: ${notification.title}")
                Result.success()
            }
        } catch (e: Exception) {
            Log.e("ServicessNotificationEntity", "NotificationEntityDecoded: ${e.message}")
            Result.retry()
        }
    }
}

