package com.ba.housedrawba.adspackage;
import static com.ba.housedrawba.adspackage.AppController.isInAppPurchased;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAppOpenAd;
import com.applovin.sdk.AppLovinSdk;
import com.ba.housedrawba.R;

public class MaxAppOpenManagerDMA implements LifecycleObserver, MaxAdListener {
    private final MaxAppOpenAd appOpenAd;
    private final Context context;

    public MaxAppOpenManagerDMA(final Context context) {
        ProcessLifecycleOwner.get().getLifecycle().addObserver( this);
        this.context = context;
        appOpenAd = new MaxAppOpenAd(context.getResources().getString(R.string.max_appOpen), context);
        appOpenAd.setListener( this );
        appOpenAd.loadAd();
        Log.d("MaxAppOpen_1","context");
    }

    private void showAdIfReady() {
        if (appOpenAd == null || !AppLovinSdk.getInstance(context).isInitialized()) return;
        if (appOpenAd.isReady()) appOpenAd.showAd();
        else appOpenAd.loadAd();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (!isInAppPurchased) showAdIfReady();
        Log.d("MaxAppOpen_1","onStart");
    }

    @Override
    public void onAdLoaded(@NonNull final MaxAd ad) {
        Log.d("MaxAppOpen_1","onAdLoaded");
    }
    @Override
    public void onAdLoadFailed(@NonNull final String adUnitId, @NonNull final MaxError error) {
        Log.e("MaxAppOpen_1","onAdLoadFailed");
    }
    @Override
    public void onAdDisplayed(@NonNull final MaxAd ad) {
        Log.d("MaxAppOpen_1","onAdDisplayed");
    }
    @Override
    public void onAdClicked(@NonNull final MaxAd ad) {

    }
    @Override
    public void onAdHidden(@NonNull final MaxAd ad) {
        appOpenAd.loadAd();
        Log.d("MaxAppOpen_1","onAdHidden");
    }

    @Override
    public void onAdDisplayFailed(@NonNull final MaxAd ad, @NonNull final MaxError error) {
        Log.e("MaxAppOpen_1","onAdDisplayFailed");
        appOpenAd.loadAd();
    }
}
