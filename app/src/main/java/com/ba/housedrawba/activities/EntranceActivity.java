package com.ba.housedrawba.activities;
import static com.ba.housedrawba.adspackage.AppController.ADMOB_APP_OPEN;
import static com.ba.housedrawba.adspackage.AppController.ADMOB_APP_OPEN_HF;
import static com.ba.housedrawba.adspackage.AppController.ADMOB_APP_OPEN_MF;
import static com.ba.housedrawba.adspackage.AppController.ADMOB_INTERSTITIAL;
import static com.ba.housedrawba.adspackage.AppController.ADMOB_INTERSTITIAL_HF;
import static com.ba.housedrawba.adspackage.AppController.ADMOB_INTERSTITIAL_MF;
import static com.ba.housedrawba.adspackage.AppController.IN_APP_PURCHASE_KEY;
import static com.ba.housedrawba.adspackage.AppController.SPLASH_AD_TYPE;
import static com.ba.housedrawba.adspackage.AppController.isInAppPurchased;
import static com.ba.housedrawba.adspackage.AppController.isShowingInterstitialAd;
import static com.ba.housedrawba.adspackage.AppController.isSplashBannerAdEnable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.Utils.PrefManager;
import com.ba.housedrawba.Utils.SharedPreferenceManager;
import com.ba.housedrawba.adspackage.AppController;
import com.ba.housedrawba.databinding.ActivitySplashBinding;
import com.ba.housedrawba.models.DataSaving;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;
@SuppressLint("CustomSplashScreen")
public class EntranceActivity extends BaseActivity {
    Activity activity = EntranceActivity.this;
    ActivitySplashBinding binding;

    public SharedPreferenceManager sharedPreferenceManager;
    public static int FIRST_TIME_RATINGS_DIALOG_LIMIT = 3;
    public static final String COUNTER_FOR_FIRST_TIME_RATINGS_DIALOG = "ratingDialogFirstTimeCounter";
    public static long SP_RATING_API_DIALOG_INTERVAL = 24*60*60*1000;
    public static String SP_LAST_RATING_API_DIALOG_VIEW_TIME = "lastRatingAPIDialogViewTime";

    public boolean isGDPRCleared = false;
    private PrefManager prefManager;
    /*..................*/
    InterstitialAd loadedInterstitialAd;
    AppOpenAd loadedAppOpenAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefManager = new PrefManager(activity);
        sharedPreferenceManager = new SharedPreferenceManager(this,IN_APP_PURCHASE_KEY);

        DataSaving dataSaving = new DataSaving();
        int counter = dataSaving.getIntPrefValue(this, COUNTER_FOR_FIRST_TIME_RATINGS_DIALOG);
        dataSaving.setIntPrefValue(this, COUNTER_FOR_FIRST_TIME_RATINGS_DIALOG, ++counter);

        if (!isInAppPurchased) {
            initGDPRDialog(activity);
        } else {
            binding.llView.setVisibility(View.GONE);
            new Handler().postDelayed(this::moveToNextActivity, 3500);
        }

        binding.startButton.setOnClickListener(v -> {
            if (!isInAppPurchased){
                if (SPLASH_AD_TYPE==1){
                    if (loadedInterstitialAd!=null) loadedInterstitialAd.show(activity);
                    else moveToNextActivity();
                }else{
                    if (loadedAppOpenAd!=null) loadedAppOpenAd.show(activity);
                    else moveToNextActivity();
                }
            }else{
                moveToNextActivity();
            }
        });
    }

    public void moveToNextActivity() {
        if (!prefManager.isFirstTimeLaunch()) {
            startActivity(new Intent(activity, HomeActivity.class));
        }else{
            startActivity(new Intent(activity, IntroScreensActivity.class));
        }
        finish();
    }


    public void initGDPRDialog(Activity activity) {
        ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(activity)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
//                .addTestDeviceHashedId("62FBE61399C9207A308F43FB62346591")//S9
                .addTestDeviceHashedId("C93498063EB041C8A6E900DB5F58604E")//s22
//                .addTestDeviceHashedId("0E99FA6D66E33CB2A76B8BE026A44F3A")//s21
                .build();

        ConsentRequestParameters params = new ConsentRequestParameters.Builder()
//                .setConsentDebugSettings(debugSettings)
                .setTagForUnderAgeOfConsent(false)
                .build();
        ConsentInformation consentInformation = UserMessagingPlatform.getConsentInformation(activity);
//        consentInformation.reset();
        consentInformation.requestConsentInfoUpdate(activity, params, () ->
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity, loadAndShowError -> {
                    if (loadAndShowError != null) {
                        // Consent gathering failed.
                        Log.d("TAGSplashConsentDi_", String.format("%s: %s",
                                loadAndShowError.getErrorCode(),
                                loadAndShowError.getMessage()));
                    }
                    if (consentInformation.isConsentFormAvailable()) {
//                      initializeMobileAdsSdk();
                        loadForm(activity,consentInformation);
                    }
                    if (consentInformation.canRequestAds()) {
                        isGDPRCleared = true;
                        loadingAppOpenAd();
//                        initializedInterface.onAdNetworkInitialized();
                        /*loadAndShowMaxInterstitialAd(activity, new AppController.InterstitialCustomAdListener() {
                            @Override
                            public void onAdClosedListener() {
                                moveToNextActivity();
                            }
                            @Override
                            public void onAdFailedToLoad(String errorMsg) {
                                moveToNextActivity();
                            }
                        });*/
                        if (isSplashBannerAdEnable) AppController.loadSplashBannerAd(binding.adView);
                        binding.llView.setVisibility(View.VISIBLE);
                        if (SPLASH_AD_TYPE==1) loadInterstitialAd();
                        else loadingAppOpenAd();
                    }
                    // Consent has been gathered.
                }), requestConsentError -> {
            // Consent gathering failed.
            Log.e("TAGSplashConsentDi_2", String.format("%s: %s",
                    requestConsentError.getErrorCode(),
                    requestConsentError.getMessage()));
        });
    }
    public void loadForm(Activity activity,ConsentInformation consentInformation) {
        UserMessagingPlatform.loadConsentForm(activity, consentForm -> {
                    if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                        consentForm.show(activity, formError -> {
                            if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.OBTAINED) {
                                // App can start requesting ads.

                            }
                            Log.d("TAGSplashConsentDi_", "phase 3 - "+consentInformation.getConsentStatus());

                            loadForm(activity,consentInformation);
                        });
                    }
                }, formError -> {
                    // Handle Error.
                    Log.e("TAGSplashConsentDi_3", String.format("%s: %s",
                            formError.getErrorCode(),
                            formError.getMessage()));
                }
        );
    }

    private void loadingAppOpenAd() {
        FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                moveToNextActivity();
            }
            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                moveToNextActivity();
            }
        };
        AdRequest request = new AdRequest.Builder().setHttpTimeoutMillis(5000).build();

        AppOpenAd.load(this, ADMOB_APP_OPEN_HF, request,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        AppOpenAd.load(activity, ADMOB_APP_OPEN_MF, request,
                                new AppOpenAd.AppOpenAdLoadCallback() {
                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        super.onAdFailedToLoad(loadAdError);
                                        AppOpenAd.load(activity, ADMOB_APP_OPEN, request,
                                                new AppOpenAd.AppOpenAdLoadCallback() {
                                                    @Override
                                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                                        super.onAdFailedToLoad(loadAdError);
                                                        showToast("Failed to load Ads");
                                                        moveToNextActivity();
                                                    }
                                                    @Override
                                                    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                                                        super.onAdLoaded(appOpenAd);
                                                        loadedAppOpenAd = appOpenAd;
                                                        loadedAppOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                                                        if (isSplashBannerAdEnable){
                                                            binding.llView.setVisibility(View.GONE);
                                                            binding.startButton.setVisibility(View.VISIBLE);
                                                        }else{
                                                            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                                                            appOpenAd.show(activity);
                                                        }

                                                    }
                                                });
                                    }
                                    @Override
                                    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                                        super.onAdLoaded(appOpenAd);
                                        loadedAppOpenAd = appOpenAd;
                                        loadedAppOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                                        if (isSplashBannerAdEnable){
                                            binding.llView.setVisibility(View.GONE);
                                            binding.startButton.setVisibility(View.VISIBLE);
                                        }else{
                                            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                                            appOpenAd.show(activity);
                                        }
                                    }
                                });

                    }
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                        super.onAdLoaded(appOpenAd);
                        loadedAppOpenAd = appOpenAd;
                        loadedAppOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                        if (isSplashBannerAdEnable){
                            binding.llView.setVisibility(View.GONE);
                            binding.startButton.setVisibility(View.VISIBLE);
                        }else{
                            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                            appOpenAd.show(activity);
                        }
                    }
                });
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().setHttpTimeoutMillis(5000).build();
        FullScreenContentCallback listener = new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                isShowingInterstitialAd = false;
                moveToNextActivity();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                super.onAdFailedToShowFullScreenContent(adError);
                isShowingInterstitialAd = false;
                moveToNextActivity();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent();
                isShowingInterstitialAd = true;
            }
        };

        InterstitialAd.load(activity, ADMOB_INTERSTITIAL_HF,
                adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        Log.d("779_interHighFloor_", "onAdLoaded");
                        loadedInterstitialAd = interstitialAd;
                        loadedInterstitialAd.setFullScreenContentCallback(listener);
                        if (isSplashBannerAdEnable){
                            binding.llView.setVisibility(View.GONE);
                            binding.startButton.setVisibility(View.VISIBLE);
                        }else{
                            interstitialAd.setFullScreenContentCallback(listener);
                            interstitialAd.show(activity);
                        }
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e("779_interHighFloor_", loadAdError.getMessage());
                        InterstitialAd.load(activity, ADMOB_INTERSTITIAL_MF, adRequest,
                                new InterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                        Log.i("779_interMediumFloor_", "onAdLoaded");
                                        loadedInterstitialAd = interstitialAd;
                                        loadedInterstitialAd.setFullScreenContentCallback(listener);
                                        if (isSplashBannerAdEnable){
                                            binding.llView.setVisibility(View.GONE);
                                            binding.startButton.setVisibility(View.VISIBLE);
                                        }else{
                                            interstitialAd.setFullScreenContentCallback(listener);
                                            interstitialAd.show(activity);
                                        }
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        Log.i("779_interMediumFloor_", loadAdError.getMessage());
                                        InterstitialAd.load(activity, ADMOB_INTERSTITIAL, adRequest,
                                                new InterstitialAdLoadCallback() {
                                                    @Override
                                                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                                        Log.i("779_interNormal_", "onAdLoaded");
                                                        loadedInterstitialAd = interstitialAd;
                                                        loadedInterstitialAd.setFullScreenContentCallback(listener);
                                                        if (isSplashBannerAdEnable){
                                                            binding.llView.setVisibility(View.GONE);
                                                            binding.startButton.setVisibility(View.VISIBLE);
                                                        }else{
                                                            interstitialAd.setFullScreenContentCallback(listener);
                                                            interstitialAd.show(activity);
                                                        }
                                                    }
                                                    @Override
                                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                                        Log.i("779_interNormal_", loadAdError.getMessage());
                                                        Toast.makeText(activity, "No Ads Available", Toast.LENGTH_SHORT).show();
                                                        moveToNextActivity();
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }


}