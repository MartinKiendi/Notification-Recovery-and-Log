package com.example.servicesandroid.room

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface NotificationRepository {
    suspend fun insertNotification(notification: Notification)

    fun getAllNotifications(): Flow<List<Notification>>

    suspend fun delete(notification: Notification)

    suspend fun clearAllNotification()
}

class NotificationRepositoryImpl @Inject constructor(private val notificationDao: NotificationDao): NotificationRepository{
    override suspend fun insertNotification(notification: Notification) {
        notificationDao.insertNotification(notification)
    }

    override fun getAllNotifications(): Flow<List<Notification>> {
        return notificationDao.getAllNotifications()
    }

    override suspend fun delete(notification: Notification) {
        notificationDao.deleteNotification(notification)
    }

    override suspend fun clearAllNotification() {
        notificationDao.clearAllNotifications()
    }
}