package com.example.servicesandroid

import android.app.Notification
import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.servicesandroid.room.convertNotificationToEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class NotificationListener : NotificationListenerService() {
    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (isBlacklistedNotification(sbn)){
            return
        }
        if (sbn?.notification?.category == Notification.CATEGORY_SYSTEM){
            return
        }

        val notificationEntity = sbn?.let { convertNotificationToEntity(this, it,false) }
        notificationEntity?.let { thisNotification ->
            val jsonString = Json.encodeToString(thisNotification)
            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInputData(workDataOf("notification" to jsonString))
                .build()
            Log.e("ServicessNotificationEntity", "Notification Detected: ${notificationEntity.title}")
            WorkManager.getInstance(applicationContext).enqueue(workRequest)
        }
    }
}
