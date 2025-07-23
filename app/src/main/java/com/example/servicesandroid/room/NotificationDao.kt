package com.example.servicesandroid.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)

    @Query("SELECT * FROM notification_table")
    fun getAllNotifications(): Flow<List<Notification>>

    @Delete
    suspend fun deleteNotification(notification: Notification)

    @Query("DELETE FROM notification_table")
    suspend fun clearAllNotifications()
}
