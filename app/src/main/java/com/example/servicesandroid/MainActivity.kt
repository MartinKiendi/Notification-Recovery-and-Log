package com.example.servicesandroid

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.servicesandroid.googleads.GoogleMobileAdsConsentManager
import com.google.android.ump.FormError
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.atomic.AtomicBoolean

const val TAG = "AdRepository"
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val notificationViewModel: NotificationViewModel by viewModels()
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private val gatherConsentFinished = AtomicBoolean(false)
    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
        enableEdgeToEdge()

        val componentName = ComponentName(this, NotificationListener::class.java)
        if (!isNotificationServiceEnabled(this, componentName)) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            startActivity(intent)
        } else {
            if (!notificationViewModel.userPreferencesFlow.value.isNotificationListenerEnabled) {
                notificationViewModel.setIfNotificationListenerEnabled(true)
            }
        }
        val languageCode = getLanguageCode(applicationContext)
        val languageForSettings = getLanguageFromCode(languageCode)
        //Toast.makeText(this, languageCode, Toast.LENGTH_SHORT).show()
        notificationViewModel.setAppLanguage(languageForSettings.name)

        googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(applicationContext)

        googleMobileAdsConsentManager.gatherConsent(this) { consentError: FormError? ->
            if (consentError != null) {
                // Consent not obtained in current session.
                Log.d(TAG, "${consentError.errorCode}: ${consentError.message}")
            }

            gatherConsentFinished.set(true)

            if (googleMobileAdsConsentManager.canRequestAds) {
                // Kick off initialization using the lifecycle coroutine scope
                AdRepository.initialize(applicationContext, lifecycleScope)
            }

        }

        // This sample attempts to load ads using consent obtained in the previous session.
        if (googleMobileAdsConsentManager.canRequestAds) {
            // Kick off initialization using the lifecycle coroutine scope
            AdRepository.initialize(applicationContext, lifecycleScope)
        }


        // Attempt to show an ad via the repository framework
        setContent {
            HomeApp(googleMobileAdsConsentManager = googleMobileAdsConsentManager)
        }
    }


}

