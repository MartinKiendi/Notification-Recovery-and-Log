package com.example.servicesandroid

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val notificationViewModel: NotificationViewModel by viewModels()


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
            if (notificationViewModel.userPreferencesFlow.value.isNotificationListenerEnabled == false) {
                notificationViewModel.setIfNotificationListenerEnabled(true)
            }
        }
        val languageCode = getLanguageCode(applicationContext)
        val localeOptions = listOf<Pair<String, String>>(
            "en" to "English",
            "de" to "Deutsch",
            "fr" to "Français",
            "it" to "Italiano",
            "es" to "Español"
        )
        val language = localeOptions.find { it.first == languageCode }?.second

        Log.e("languageCode", languageCode)
        Log.e("language", language.toString())
        notificationViewModel.setAppLanguage(language.toString())

        CoroutineScope(Dispatchers.IO).launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {}
        }
        val ads = downloadAds(this)
        setContent {
            HomeApp(
                listOfAds = ads,
            )
        }
    }

    private fun downloadAds(context: Context): ArrayList<NativeAd> {
        val builder = AdLoader.Builder(context, NATIVE_AD_UNIT_ID)
        val ads: ArrayList<NativeAd> = arrayListOf()

        builder.forNativeAd { ad: NativeAd ->
            ads.add(ad)
        }
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Handle the failure by logging, altering the UI, and so on.
                val error =
                    """domain: ${adError.domain}, 
                       code: ${adError.code},
                       message: ${adError.message}
                    """"
                Toast.makeText(
                    this@MainActivity,
                    "Failed to load native ad with error $error",
                    Toast.LENGTH_SHORT,
                ).show()
                Log.e("AdError", "onAdFailedToLoad: $error", )
            }

        }).withNativeAdOptions(
            NativeAdOptions.Builder()
                // Methods in the NativeAdOptions.Builder class can be
                // used here to specify individual options settings.
                .build()
        )
            .build()

        adLoader.loadAds(AdRequest.Builder().build(), 3)
        return ads
    }

}

