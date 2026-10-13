package com.example.servicesandroid


import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.AdView
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadResult
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.common.PreloadConfiguration
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdPreloader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AdRepository {
    private const val TAG = "AdRepository"
    private const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"
    private const val TEST_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    private var isInitialized = false

    /**
     * Initializes the GMA SDK and triggers an initial preload if online.
     */
    fun initialize(context: Context, scope: CoroutineScope) {
        if (isInitialized) return

        if (!isDeviceOnline(context)) {
            Log.d(TAG, "Device offline during initialization. Preloading skipped.")
            return
        }

        scope.launch(Dispatchers.IO) {
            MobileAds.initialize(
                context.applicationContext,
                InitializationConfig.Builder(TEST_APP_ID).build()
            ) {
                isInitialized = true
                Log.d(TAG, "GMA SDK Initialized. Starting preloader.")
                startPreloading()
            }
        }
    }

    /**
     * Instructs the Next-Gen preloader to begin caching an ad item.
     */
    private fun startPreloading() {
        val adRequest = AdRequest.Builder(TEST_AD_UNIT_ID).build()
        val preloadConfig = PreloadConfiguration(adRequest)
        InterstitialAdPreloader.start(TEST_AD_UNIT_ID, preloadConfig)
    }

    /**
     * Safe entry-point for the UI layer to acquire a cached Interstitial Ad.
     * Evaluates network state proactively before querying the SDK cache.
     */
    fun fetchPreloadedAd(context: Context): InterstitialAd? {
        if (!isDeviceOnline(context)) {
            Log.d(TAG, "Device is offline. Rejecting ad retrieval.")
            return null
        }

        return InterstitialAdPreloader.pollAd(TEST_AD_UNIT_ID)
    }

    /**
     * Utility method verifying whether active data transport lanes exist.
     */
    private fun isDeviceOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }


}
fun tryDisplayingAdFlow(context: Context, activity : Activity?, proceedToContent : () -> Unit) {
    val ad = AdRepository.fetchPreloadedAd(context)

    if (ad != null) {
        ad.adEventCallback = object : InterstitialAdEventCallback {
            override fun onAdClicked() {
                super.onAdClicked()
                proceedToContent()
            }
            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad rendering on screen.")
                proceedToContent()
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad dismissed by user.")
                proceedToContent()
            }

            override fun onAdFailedToShowFullScreenContent(fullScreenContentError: FullScreenContentError) {
                Log.e(TAG, "Ad failed mid-display: ${fullScreenContentError.message}")
                proceedToContent()
            }
        }
        if (activity != null){
            ad.show(activity)
        }
    } else {
        Log.d(TAG, "Repository returned no ad. Seamless content transition.")
        proceedToContent()
    }
}




@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adUnitId: String = "ca-app-pub-3940256099942544/6300978111" // Test ID
) {
    // Initialize required variables.
    val context = LocalContext.current
    val activity = LocalActivity.current
    var bannerAdState by remember { mutableStateOf<BannerAd?>(null) }

    // Request a large anchored adaptive banner with a width of 360.
    val adSize = AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(context, 360)

// Load the ad when the screen is active.
    val coroutineScope = rememberCoroutineScope()
    val isPreviewMode = LocalInspectionMode.current
    LaunchedEffect(context) {
        bannerAdState?.destroy()
        if (!isPreviewMode) {
            coroutineScope.launch {
                when (val result = BannerAd.load(BannerAdRequest.Builder(adUnitId, adSize).build())) {
                    is AdLoadResult.Success -> {
                        bannerAdState = result.ad
                    }
                    is AdLoadResult.Failure -> {
                        Log.w(TAG, "Banner ad failed to load: $result.error")
                    }
                }
            }
        }
    }

        bannerAdState?.let { bannerAd ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Display the ad within an AndroidView.
                AndroidView(
                    modifier = modifier.wrapContentSize(),
                    factory = { bannerAd.getView(activity!!) },
                )
            }
        }
    DisposableEffect(Unit) { onDispose { bannerAdState?.destroy() } }
}


const val NATIVE_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

/*
fun showInterstitialAd(
    context: Context,
    activity: Activity?,
    goToPreviousPage: () -> Unit
) {
    if (!checkForInternet(context)) {
        goToPreviousPage()
    } else{
        InterstitialAd.load(
            context,
            AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    goToPreviousPage()
                }
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdClicked() {
                            super.onAdClicked()
                            goToPreviousPage()
                        }

                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                            goToPreviousPage()
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            super.onAdFailedToShowFullScreenContent(p0)
                            goToPreviousPage()
                        }
                    }
                    if (activity != null) {
                        interstitialAd.show(activity)
                    }
                }
            },
        )
    }

}
@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.size(12.dp))
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    // Use test ad unit ID
                    adUnitId = AD_UNIT_ID
                    loadAd(AdRequest.Builder().build())
                }
            },
        )
        Spacer(modifier = Modifier.size(12.dp))
    }

}
@Composable
fun NativeAdJetPack(
    nativeAd: NativeAd?,
    modifier: Modifier = Modifier
){
    AndroidView(
        factory = { context ->
            val adView = LayoutInflater.from(context)
                .inflate(
                R.layout.ad_unified,
                null
            ) as NativeAdView

            val adHeadline = adView.findViewById<TextView>(R.id.ad_headline)
            val adBody = adView.findViewById<TextView>(R.id.ad_body)
            val adCallToAction = adView.findViewById<Button>(R.id.ad_call_to_action)
            val adAppIcon = adView.findViewById<ImageView>(R.id.ad_app_icon)
            val adPrice = adView.findViewById<TextView>(R.id.ad_price)
            val adStars = adView.findViewById<RatingBar>(R.id.ad_stars)
            val adStore = adView.findViewById<TextView>(R.id.ad_store)
            val adAdvertiser = adView.findViewById<TextView>(R.id.ad_advertiser)

            // The headline and media content are guaranteed to be in every UnifiedNativeAd.
            adHeadline.text = nativeAd?.headline

            // Set the media view.
            //nativeAdView.mediaView = unifiedAdBinding.adMedia

            // Set other ad assets.
            adView.headlineView = adHeadline
            adView.bodyView = adBody
            adView.callToActionView = adCallToAction
            adView.iconView = adAppIcon
            adView.priceView = adPrice
            adView.starRatingView = adStars
            adView.storeView = adStore
            adView.advertiserView = adAdvertiser



            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd?.body == null) {
                adBody.visibility = View.INVISIBLE
            } else {
                adBody.visibility = View.VISIBLE
                adBody.text = nativeAd.body
            }

            if (nativeAd?.callToAction == null) {
                adCallToAction.visibility = View.INVISIBLE
            } else {
                adCallToAction.visibility = View.VISIBLE
                adCallToAction.text = nativeAd.callToAction
            }

            if (nativeAd?.icon == null) {
                adAppIcon.visibility = View.GONE
            } else {
                adAppIcon.setImageDrawable(nativeAd.icon?.drawable)
                adAppIcon.visibility = View.VISIBLE
            }

            if (nativeAd?.price == null) {
                adPrice.visibility = View.INVISIBLE
            } else {
                adPrice.visibility = View.VISIBLE
                adPrice.text = nativeAd.price
            }

            if (nativeAd?.store == null) {
                adStore.visibility = View.INVISIBLE
            } else {
                adStore.visibility = View.VISIBLE
                adStore.text = nativeAd.store
            }

            if (nativeAd?.starRating == null) {
                adStars.visibility = View.INVISIBLE
            } else {
                adStars.rating = nativeAd.starRating!!.toFloat()
                adStars.visibility = View.VISIBLE
            }

            if (nativeAd?.advertiser == null) {
                adAdvertiser.visibility = View.INVISIBLE
            } else {
                adAdvertiser.text = nativeAd.advertiser
                adAdvertiser.visibility = View.VISIBLE
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            nativeAd?.let { adView.setNativeAd(it) }
            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.
            val mediaContent = nativeAd?.mediaContent
            val vc = mediaContent?.videoController

            // Updates the UI to say whether or not this ad has a video asset.
            if (vc != null && mediaContent.hasVideoContent()) {
                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
                // VideoController will call methods on this object when events occur in the video
                // lifecycle.
                vc.videoLifecycleCallbacks =
                    object : VideoController.VideoLifecycleCallbacks() {
                        override fun onVideoEnd() {
                            // Publishers should allow native ads to complete video playback before
                            // refreshing or replacing them with another ad in the same UI location.
                            super.onVideoEnd()
                        }
                    }
            } else {
                //mainActivityBinding.videostatusText.text = "Video status: Ad does not contain a video asset."
                //mainActivityBinding.refreshButton.isEnabled = true
            }
            adView
        },
        modifier = modifier.wrapContentHeight().fillMaxWidth()
    )
}
*/


