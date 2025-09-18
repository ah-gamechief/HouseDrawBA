package com.ba.housedrawba.adspackage;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkInitializationConfiguration;
import com.ba.housedrawba.R;
import com.ba.housedrawba.Utils.SharedPreferenceManager;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.Map;

public class AppController extends Application {
    public Context sContext;
    public static String PRODUCT_ID ;
    public static boolean isInAppPurchased = false;
    public static String IN_APP_PURCHASE_KEY = "is_in_app_purchased";
    static String packageName;
    public static int adClickCounter = 0;
    public static int adsShowInterval = 3;
    public static boolean isInternetConnected = false;
    public static boolean isShowingInterstitialAd = false;
//    public AdNetworkInitializedInterface initializedInterface;

    public static String ADMOB_APP_ID;
    public static String ADMOB_BANNER_SPLASH;
    public static String ADMOB_BANNER;
    public static String ADMOB_BANNER_MF;
    public static String ADMOB_BANNER_HF;
    public static String ADMOB_INTERSTITIAL;
    public static String ADMOB_INTERSTITIAL_MF;
    public static String ADMOB_INTERSTITIAL_HF;
    public static String ADMOB_APP_OPEN;
    public static String ADMOB_APP_OPEN_MF;
    public static String ADMOB_APP_OPEN_HF;
    public static String ADMOB_REWARDED;
    public static String ADMOB_REWARDED_HF;
    public static String ADMOB_REWARDED_MF;

    public static boolean isShowAdOnInAppClose = false;
    public static boolean isShowAdaptiveBanner = true;
    public static boolean isShowAdOnLoadScreenNextBtn = false;
    public static boolean isAdmobInterstitialFlooringEnabled = false;
    public static boolean isSplashBannerAdEnable = true;//done
    public static boolean isFirebaseEnabled = true;
    public static int appOpenAdType = 1; // 0 for admob, 1 for max
    public static int defaultAdsNetwork = 1; // 0 for admob, 1 for max, 2 mix
    public static int SPLASH_AD_TYPE = 0;//0 for appOpen,1 for interstitial, 2 for max

    public interface AdNetworkInitializedInterface{
        void onAdNetworkInitialized();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAnalytics.getInstance(this);
        sContext = getApplicationContext();
        packageName = sContext.getPackageName();
        PRODUCT_ID = sContext.getString(R.string.PURCHASE_ID);

        ADMOB_APP_ID = sContext.getResources().getString(R.string.ADMOB_APP_ID);
        ADMOB_BANNER = sContext.getResources().getString(R.string.ADMOB_BANNER);
        ADMOB_BANNER_HF = sContext.getResources().getString(R.string.ADMOB_BANNER_HF);
        ADMOB_BANNER_MF = sContext.getResources().getString(R.string.ADMOB_BANNER_MF);
        ADMOB_BANNER_SPLASH = sContext.getResources().getString(R.string.ADMOB_BANNER_SPLASH);
        ADMOB_INTERSTITIAL = sContext.getResources().getString(R.string.ADMOB_INTERSTITIAL);
        ADMOB_INTERSTITIAL_HF = sContext.getResources().getString(R.string.ADMOB_INTERSTITIAL_HF);
        ADMOB_INTERSTITIAL_MF = sContext.getResources().getString(R.string.ADMOB_INTERSTITIAL_MF);
        ADMOB_APP_OPEN = sContext.getResources().getString(R.string.ADMOB_OPEN_APP);
        ADMOB_APP_OPEN_HF = sContext.getResources().getString(R.string.ADMOB_OPEN_APP_HF);
        ADMOB_APP_OPEN_MF = sContext.getResources().getString(R.string.ADMOB_OPEN_APP_MF);
        ADMOB_REWARDED = sContext.getResources().getString(R.string.ADMOB_REWARDED);
        ADMOB_REWARDED_HF = sContext.getResources().getString(R.string.ADMOB_REWARDED_HF);
        ADMOB_REWARDED_MF = sContext.getResources().getString(R.string.ADMOB_REWARDED_MF);
//        initFbRemoteConfig(this);
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(this,PRODUCT_ID);
        isInAppPurchased = sharedPreferenceManager.getBooleanValue(IN_APP_PURCHASE_KEY, false);

        MobileAds.initialize(this, initializationStatus -> {
            Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
            for (String adapterClass : statusMap.keySet()) {
                AdapterStatus status = statusMap.get(adapterClass);
                if (status != null) {
                    Log.d("MobileAds_initialize_", String.format(
                            "Adapter name: %s, Description: %s, Latency: %d",
                            adapterClass, status.getDescription(), status.getLatency()));
                } else {
                    Log.e("MobileAds_initialize_", "null values");
                }
            }
            new AppOpenManager(this);
        });
    }

    public void initAppLovinAdsSDK(Context context){
        AppLovinSdkInitializationConfiguration initConfig = AppLovinSdkInitializationConfiguration
                .builder(context.getString(R.string.max_sdk),context)
                .setMediationProvider(AppLovinMediationProvider.MAX).build();
        // Initialize the SDK with the configuration
        AppLovinSdk.getInstance(context).initialize(initConfig, sdkConfig -> {
            // Start loading ads
//                                initializedInterface.onAdNetworkInitialized();
            Log.d("MaxAppOpen_1", "AppLovin SDK is initialized, start loading ads");
            new MaxAppOpenManagerDMA(context);
        });
    }

    /*__________________________________________________________________________________*/
    /*----------Banner Ad Section--------------*/
    public static void createAndGetAdaptiveAd(Activity activity, FrameLayout frameLayout) {
        if (!isInAppPurchased) {
            AdSize adSize = getAdSize(activity);
            AdRequest adRequest = getBannerAd(2);
            loadHighFloorBanner(frameLayout, adRequest, adSize);

            /*Log.e("MaxBanner_1", "Max Banner Triggered");
            MaxAdView adView = new MaxAdView(activity.getResources().getString(R.string.max_banner), activity);
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int heightPx = activity.getResources().getDimensionPixelSize(R.dimen.max_banner_size);
            adView.setLayoutParams( new FrameLayout.LayoutParams( width, heightPx ) );
            adView.setBackgroundColor(activity.getResources().getColor(R.color.white, activity.getTheme()));
            frameLayout.addView(adView);
            adView.loadAd();
            adView.setListener(new MaxAdViewAdListener() {
                @Override
                public void onAdLoaded(@NonNull MaxAd maxAd) {
//                            frameLayout.removeView(textView);
                    Log.e("MaxBanner_1", "on Ad Loaded");
                }
                @Override
                public void onAdExpanded(@NonNull MaxAd maxAd) {
                }
                @Override
                public void onAdCollapsed(@NonNull MaxAd maxAd) {
                }
                @Override
                public void onAdDisplayed(@NonNull MaxAd maxAd) {
                }
                @Override
                public void onAdHidden(@NonNull MaxAd maxAd) {
                    Log.e("MaxBanner_1", "on Ad Hidden");
                }
                @Override
                public void onAdClicked(@NonNull MaxAd maxAd) {
                }
                @Override
                public void onAdLoadFailed(@NonNull String s, @NonNull MaxError maxError) {
                    Log.e("MaxBanner_1", "Failed to load - "+maxError.getMessage());
                }
                @Override
                public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {
                    Log.e("MaxBanner_1", "Failed to display - "+maxError.getMessage());
                }
            });*/
        }
    }

    public static void loadSplashBannerAd(FrameLayout frameLayout) {
        if (!isInAppPurchased) {
            AdSize adSize = AdSize.LARGE_BANNER;
            AdRequest adRequest = new AdRequest.Builder().setHttpTimeoutMillis(6000).build();
            loadNormalBanner(frameLayout, adRequest, adSize);

            AdView adView = new AdView(frameLayout.getContext());
            adView.setAdSize(adSize);
            adView.setAdUnitId(ADMOB_BANNER_SPLASH);
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    adView.destroy();
                    Log.e("775_bannerAd",loadAdError.getMessage());
                }
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    frameLayout.addView(adView);
                    Log.d("775_bannerAd","Normal floor banner loaded");
                }
            });
        }
    }
    public static void loadBigBannerAd(Activity activity, FrameLayout frameLayout) {
        if (!isInAppPurchased) {
            AdSize adSize = AdSize.MEDIUM_RECTANGLE;
            AdRequest adRequest = new AdRequest.Builder().setHttpTimeoutMillis(3000).build();
            loadHighFloorBanner(frameLayout, adRequest, adSize);

/*            MaxAdView maxAdView = new MaxAdView(activity.getResources().getString(R.string.max_MREC), MaxAdFormat.MREC, activity);
            // MREC width and height are 300 and 250 respectively, on phones and tablets
            int widthPx = AppLovinSdkUtils.dpToPx(activity, 300);
            int heightPx = AppLovinSdkUtils.dpToPx(activity, 250);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(widthPx, heightPx);
            layoutParams.gravity = Gravity.CENTER;
            maxAdView.setLayoutParams(layoutParams);
            maxAdView.setGravity(Gravity.CENTER);
            // Set background or background color for MRECs to be fully functional
            frameLayout.addView(maxAdView);
            // Load the ad
            maxAdView.loadAd();*/
        }
    }

    private static void loadNormalBanner(FrameLayout frameLayout,AdRequest adRequest,AdSize adSize){
        AdView adView = new AdView(frameLayout.getContext());
        adView.setAdSize(adSize);
        adView.setAdUnitId(ADMOB_BANNER);
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adView.destroy();
                Log.e("775_bannerAd",loadAdError.getMessage());
            }
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                frameLayout.addView(adView);
                Log.d("775_bannerAd","Normal floor banner loaded");
            }
        });

    }

    private static void loadHighFloorBanner(FrameLayout frameLayout,AdRequest adRequest,AdSize adSize){
        AdView adView = new AdView(frameLayout.getContext());
        adView.setAdSize(adSize);
        adView.setAdUnitId(ADMOB_BANNER_HF);
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adView.destroy();
                loadMediumFloorBanner(frameLayout,adRequest,adSize);
                Log.e("775_bannerAd",loadAdError.getMessage());
            }
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                frameLayout.addView(adView);
                Log.d("775_bannerAd","High floor banner loaded");
            }
        });
    }
    private static void loadMediumFloorBanner(FrameLayout frameLayout,AdRequest adRequest,AdSize adSize){
        AdView adView = new AdView(frameLayout.getContext());
        adView.setAdSize(adSize);
        adView.setAdUnitId(ADMOB_BANNER_MF);
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adView.destroy();
                loadNormalBanner(frameLayout,adRequest,adSize);
                Log.e("775_bannerAd",loadAdError.getMessage());
            }
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                frameLayout.addView(adView);
                Log.d("775_bannerAd","Medium floor banner loaded");
            }
        });
    }
    public static AdRequest getBannerAd(int type){
        if (isShowAdaptiveBanner){
            Bundle extras = new Bundle();
            if (type == 1){
                extras.putString("collapsible", "top");
            }else {
                extras.putString("collapsible", "bottom");
            }
            return new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .setHttpTimeoutMillis(4000)
                    .build();
        }
        else{
            return new AdRequest.Builder().setHttpTimeoutMillis(3000).build();
        }
    }
    private static AdSize getAdSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }
    /*__________________________________________________________________________________*/
    /*----------Interstitial Ad Section--------------*/
    public static void showInterstitialAdWithLoader(Activity activity, InterstitialCustomAdListener listener){
        if (!isInAppPurchased){
            loadNShowAdmobInterstitialAdWithLoader(activity, listener);
//            loadNShowMaxInterstitialAdWithLoader(activity, listener);

        }
    }

    public static void loadNShowAdmobInterstitialAdWithLoader(Activity activity, InterstitialCustomAdListener listener){
        if (!isInAppPurchased) {
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.layout_dialog_interstitial_loader);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            if (dialog.getWindow()!=null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            dialog.show();

            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    listener.onAdClosedListener();
                    isShowingInterstitialAd = false;
                }
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    listener.onAdFailedToLoad(adError.getMessage());
                    isShowingInterstitialAd = false;
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    isShowingInterstitialAd = true;
                }
            };

            AdRequest adRequest = new AdRequest.Builder().setHttpTimeoutMillis(3000).build();
            InterstitialAd.load(activity, ADMOB_INTERSTITIAL_HF,
                    adRequest, new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            Log.d("779_interHighFloor_", "onAdLoaded");
                            interstitialAd.setFullScreenContentCallback(fullScreenContentCallback);
                            dialog.dismiss();
                            interstitialAd.show(activity);
                        }
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e("779_interHighFloor_", loadAdError.getMessage());

                            InterstitialAd.load(activity,ADMOB_INTERSTITIAL_MF, adRequest,
                                    new InterstitialAdLoadCallback() {
                                        @Override
                                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                            Log.i("779_interMediumFloor_", "onAdLoaded");
                                            interstitialAd.setFullScreenContentCallback(fullScreenContentCallback);
                                            dialog.dismiss();
                                            interstitialAd.show(activity);
                                        }
                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                            Log.i("779_interMediumFloor_", loadAdError.getMessage());
                                            InterstitialAd.load(activity,ADMOB_INTERSTITIAL, adRequest,
                                                    new InterstitialAdLoadCallback() {
                                                        @Override
                                                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                                            Log.i("779_interNormal_", "onAdLoaded");
                                                            interstitialAd.setFullScreenContentCallback(fullScreenContentCallback);
                                                            dialog.dismiss();
                                                            interstitialAd.show(activity);
                                                        }
                                                        @Override
                                                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                                            Log.i("779_interNormal_", loadAdError.getMessage());
                                                            dialog.dismiss();
                                                            Toast.makeText(activity, "No Ads Available", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });

        }
    }

    public static void showRewardedAd(Activity activity, AppController.InterstitialCustomAdListener dismissedListener) {
        String tag = "rewardedAdTAg_01";
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.layout_dialog_interstitial_loader);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow()!=null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();

        if (!isInAppPurchased) {
            AdRequest adRequest = new AdRequest.Builder().setHttpTimeoutMillis(5000).build();
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    Log.d(tag, "Ad dismissed fullscreen content.");
                    if (dismissedListener!=null) dismissedListener.onAdClosedListener();
                }
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    Log.e(tag, "Ad failed to show: ");
                }
                @Override
                public void onAdShowedFullScreenContent() {
                    Log.d(tag, "Ad showed fullscreen content.");
                }
            };

            OnUserEarnedRewardListener userEarnedRewardListener = new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                }
            };

            RewardedAd.load(activity, ADMOB_REWARDED_HF, adRequest, new RewardedAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.e(tag, "Failed High: "+loadAdError.getMessage());

                    RewardedAd.load(activity, ADMOB_REWARDED_MF, adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e(tag, "Failed Medium: "+loadAdError.getMessage());

                            RewardedAd.load(activity, ADMOB_REWARDED, adRequest, new RewardedAdLoadCallback() {
                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    Log.e(tag, "Failed Normal: "+loadAdError.getMessage());
                                    dialog.dismiss();
                                    if (dismissedListener!=null) dismissedListener.onAdClosedListener();
                                    Toast.makeText(activity, "No Ad available please try again Later", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                                    Log.d(tag, "Normal Ad loaded.");
                                    dialog.dismiss();
                                    rewardedAd.setFullScreenContentCallback(fullScreenContentCallback);
                                    rewardedAd.show(activity, userEarnedRewardListener);
                                }
                            });
                        }
                        @Override public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            dialog.dismiss();
                            rewardedAd.setFullScreenContentCallback(fullScreenContentCallback);
                            rewardedAd.show(activity, userEarnedRewardListener);
                            Log.d(tag, "High Medium Ad loaded.");
                        }
                    });
                }
                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    dialog.dismiss();
                    rewardedAd.setFullScreenContentCallback(fullScreenContentCallback);
                    rewardedAd.show(activity, userEarnedRewardListener);
                    Log.d(tag, "High Reward Ad loaded.");
                }
            });
        }
    }

    public static void createUnifiedAds(Context context, int unitId,AdRequest request, AdUnifiedListening listening) {
        AdLoader.Builder builder = new AdLoader.Builder(context,context.getString(unitId));
        builder.forNativeAd(listening);
        builder.withAdListener(listening);
        AdLoader adLoader = builder.build();
        adLoader.loadAds(request,1);
        listening.setAdLoader(adLoader);
    }

    public interface InterstitialCustomAdListener{
        void onAdClosedListener();
        void onAdFailedToLoad(String errorMsg);
    }
    /*__________________________________________________________________________________*/

    private void initFbRemoteConfig(Context sContext){
        String TAG = "mFirebaseRemoteConfig_1";
        FirebaseApp.initializeApp(sContext);
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(5000).build();

        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
//        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean updated = task.getResult();
                isFirebaseEnabled = mFirebaseRemoteConfig.getBoolean("firebaseEnabled");
                if (isFirebaseEnabled){
                    if (!mFirebaseRemoteConfig.getString("adShowInterval").isEmpty())
                        adsShowInterval = Integer.parseInt(mFirebaseRemoteConfig.getString("adShowInterval"));
                    if (!mFirebaseRemoteConfig.getString("splashAdType").isEmpty())
                        SPLASH_AD_TYPE = Integer.parseInt(mFirebaseRemoteConfig.getString("splashAdType"));
                    if (!mFirebaseRemoteConfig.getString("appOpenAdType").isEmpty())
                        appOpenAdType = Integer.parseInt(mFirebaseRemoteConfig.getString("appOpenAdType"));

                    isShowAdOnInAppClose = mFirebaseRemoteConfig.getBoolean("showAdOnInAppClose");
                    isShowAdOnLoadScreenNextBtn = mFirebaseRemoteConfig.getBoolean("showAdOnLoadScreenNextBtn");
                    isShowAdaptiveBanner = mFirebaseRemoteConfig.getBoolean("showAdaptiveBanner");
                    isAdmobInterstitialFlooringEnabled = mFirebaseRemoteConfig.getBoolean("admobInterstitialFlooringEnabled");
                    Log.d(TAG, "Config params updated: " + updated+" - "+ADMOB_APP_ID+" - "
                            + isAdmobInterstitialFlooringEnabled+" - ");
                }
            } else {
                Log.d(TAG, "Config Fetching data failed: " +" - "+ADMOB_APP_ID);
            }

        });
    }
    private void funChangeAdmobAppId(String appId){
        String TAG = "mFirebaseRemoteConfig_1";
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String myApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
            Log.d(TAG, "Name Found: " + myApiKey);
            ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", appId);
            String ApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
            Log.d(TAG, "ReNamed Found: " + ApiKey);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
    }
}