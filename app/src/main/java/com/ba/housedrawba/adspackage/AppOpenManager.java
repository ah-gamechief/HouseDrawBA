package com.ba.housedrawba.adspackage;
import static androidx.lifecycle.Lifecycle.Event.ON_START;

import static com.ba.housedrawba.adspackage.AppController.ADMOB_APP_OPEN;
import static com.ba.housedrawba.adspackage.AppController.ADMOB_APP_OPEN_HF;
import static com.ba.housedrawba.adspackage.AppController.ADMOB_APP_OPEN_MF;
import static com.ba.housedrawba.adspackage.AppController.isInAppPurchased;
import static com.ba.housedrawba.adspackage.AppController.isShowingInterstitialAd;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import com.ba.housedrawba.Utils.NetworkUtil;
import com.ba.housedrawba.activities.EntranceActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;
public class AppOpenManager implements LifecycleObserver, Application.ActivityLifecycleCallbacks {
    private final AppController myApplication;
    private AppOpenAd appOpenAd = null;
    private Activity currentActivity;
    public static boolean isShowingAd = false;
    private static boolean isAdLoaded ;
    public static boolean isAllowedOpenAppAd = true;
    private long loadTime = 0;

    public AppOpenManager(AppController myApplication) {
        this.myApplication = myApplication;
        myApplication.registerReceiver(networkChangeReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            String status = NetworkUtil.getConnectivityStatusStringAS(context);
            if (status.equals("Not connected to Internet")) {
                AppController.isInternetConnected = false;
            }else{
                AppController.isInternetConnected = true;
            }
        }
    };

    /** LifecycleObserver methods */
    @OnLifecycleEvent(ON_START)
    public void onStart() {
        if (!(currentActivity instanceof EntranceActivity) && !isInAppPurchased && !isShowingInterstitialAd) {
            if (isAllowedOpenAppAd) showAdIfAvailable();
            else isAllowedOpenAppAd = true;
        }
    }

    public void showAdIfAvailable() {
        if (!isShowingAd && isAdAvailable() && appOpenAd!=null) {
            Log.d("771_HighFloorAppOpen", "Will show ad.");

            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    AppOpenManager.this.appOpenAd = null;
                    isShowingAd = false;
                    fetchAd();
                }
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    AppOpenManager.this.appOpenAd = null;
                    isShowingAd = false;
                    Log.e("771_HighFloorAppOpen","failedToShow "+adError.getMessage());
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    isShowingAd = true;
                    AppOpenManager.this.appOpenAd = null;
                }
            };
            isShowingAd = true;
            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(currentActivity);
        }
        else {
            Log.d("771_AppOpenManager__", "Ad null loading new.");
            fetchAd();
        }
    }

    public void fetchAd() {
        if (isAdAvailable()) return;
        AdRequest request = new AdRequest.Builder().build();
//        fetchAdHighFloor(request);
        fetchNormalAd(request);
    }
    
    public void fetchAdHighFloor(AdRequest request) {
        AppOpenAd.load(myApplication,ADMOB_APP_OPEN_HF, request,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        isAdLoaded = false;
                        AppOpenManager.this.appOpenAd = null;
                        fetchAMediumFloor(request);
                    }
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                        super.onAdLoaded(appOpenAd);
                        isAdLoaded = true;
                        AppOpenManager.this.appOpenAd = appOpenAd;
                        AppOpenManager.this.loadTime = (new Date()).getTime();
                    }
                });
        Log.e("771_fetchAdHighFloor",isAdLoaded+"");
    }

    public void fetchAMediumFloor(AdRequest request) {
        AppOpenAd.load(myApplication, ADMOB_APP_OPEN_MF, request,
                new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                isAdLoaded = false;
                AppOpenManager.this.appOpenAd = null;
                fetchNormalAd(request);
            }
            @Override
            public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                super.onAdLoaded(appOpenAd);
                isAdLoaded = true;
                AppOpenManager.this.appOpenAd = appOpenAd;
                AppOpenManager.this.loadTime = (new Date()).getTime();
            }
        });
        Log.e("771_fetchAMediumFloor",isAdLoaded+"");
    }

    public void fetchNormalAd(AdRequest adRequest) {
        AppOpenAd.load(myApplication, ADMOB_APP_OPEN, adRequest,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                        super.onAdLoaded(appOpenAd);
                        isAdLoaded = true;
                        AppOpenManager.this.appOpenAd = appOpenAd;
                        AppOpenManager.this.loadTime = (new Date()).getTime();
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        isAdLoaded = false;
                        AppOpenManager.this.appOpenAd = null;
                    }
                });
        Log.d("771_fetchAdNormalFloor",isAdLoaded+"");
    }

    /** Utility method to check if ad was loaded more than n hours ago. */
    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }
    /**
     * Utility method that checks if ad exists and can be shown.
     */
    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {}

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) { currentActivity = activity; }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        isShowingAd = false;
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) { }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) { currentActivity = null; }

}