package com.example.servicesandroid

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.servicesandroid.room.NotificationRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ServiceApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var notificationWorkerFactory: NotificationWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(notificationWorkerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        myPackageManager = applicationContext.packageManager
    }

    companion object{
        lateinit var myPackageManager: PackageManager
    }

}

class NotificationWorkerFactory @Inject constructor(
    private val notificationRepository: NotificationRepository
): WorkerFactory(){
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? = NotificationWorker(
        notificationRepository = notificationRepository,
        context = appContext,
        workerParams = workerParameters
    )
}
