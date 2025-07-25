package com.example.servicesandroid

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

const val NATIVE_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

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

