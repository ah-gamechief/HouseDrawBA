package com.ba.housedrawba.activities;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.R;
import com.ba.housedrawba.Utils.PrefManager;
import com.ba.housedrawba.adspackage.AdUnifiedListening;
import com.ba.housedrawba.adspackage.AppController;
import com.ba.housedrawba.databinding.ActivityIntroScreensBinding;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

import java.util.ArrayList;
import java.util.List;
public class IntroScreensActivity extends BaseActivity {
    private ActivityIntroScreensBinding binding;
    private int[] layouts;
    private PrefManager prefManager;
    private final List<Integer> introScreenPositions = new ArrayList<>();
    private final List<NativeAd> preloadedNativeAds = new ArrayList<>();
    private static final int TOTAL_INTRO_SCREENS = 3;
    private static final int MAX_NATIVE_ADS = 2; // Range 0-4
    private final int nativeAdsToLoad = 1;
    private int nativeAdsLoaded = 0;
    private boolean allAdsPreloaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        noOfNativeBetweenIntroSlider = Math.max(0, Math.min(noOfNativeBetweenIntroSlider, MAX_NATIVE_ADS));//Range from 0 to 4
//        if (spmLanguage != null) changeLocale(this, spmLanguage.getValue("lang", "en"), spmLanguage.getIntValue("index", 0), false);

        binding = ActivityIntroScreensBinding.inflate(getLayoutInflater());
        prefManager = new PrefManager(this);
        setContentView(binding.getRoot());

        // Setup status bar
        UltimateBarX.statusBarOnly(this)
                .fitWindow(true)
                .transparent()
                .colorRes(android.R.color.transparent)
                .light(true)
                .apply();
        // Initialize small native ad

//        TemplateView templateView = new TemplateView(this);
//        templateView.setLayoutParams(binding.nativeAdViewContainer.getLayoutParams());
//        templateView.setTemplateType(com.google.android.ads.nativetemplates.R.layout.gnt_small_template_view);

        initNativeAdSingleSmallIntroScreen(this, binding.nativeAdViewContainer);
        /*switch (introScreenSmallAdType){
            case 0:
                initNativeAdSingleSmall(this, binding.nativeAdViewContainer, templateView);
                break;
            case 1:
                AppController.loadLargeBannerAd(binding.nativeAdViewContainer);
                break;
        }*/
        // Setup view pager layouts
        setupViewPagerLayouts();
        // Preload native ads if needed
        if (nativeAdsToLoad > 0) {
            showLoadingProgress();
            preloadNativeAds();
        } else {
            initializeViewPager();
        }

//        spmCredits = new SharedPreferenceManager(this, "spmCredits");
        binding.btnNext.setOnClickListener(v -> {
            int current = getItem(+1);
            if (current < layouts.length) {
                binding.viewPager.setCurrentItem(current);
            } else {
                launchHomeScreen();
            }
        });
    }


    private void showLoadingProgress() {
        // Show a progress bar or loading indicator while ads are loading
        binding.loadingView.setVisibility(View.VISIBLE);
        binding.viewPager.setVisibility(View.GONE);
        binding.btnNext.setVisibility(View.GONE);
    }

    private void hideLoadingProgress() {
        binding.loadingView.setVisibility(View.GONE);
        binding.viewPager.setVisibility(View.VISIBLE);
        binding.btnNext.setVisibility(View.VISIBLE);
    }

    private void preloadNativeAds() {
        loadNativeAdWithFallback(new NativeAdCallback() {
            @Override
            public void onAdLoaded(NativeAd nativeAd) {
                for (int i = 0; i < 2; i++) {
                    preloadedNativeAds.add(nativeAd);
                    nativeAdsLoaded++;
                }
                checkAllAdsPreloaded();
            }
            @Override
            public void onAllAttemptsFailed() {
                nativeAdsLoaded++;
                checkAllAdsPreloaded();
            }
        });
    }

    private void checkAllAdsPreloaded() {
        if (nativeAdsLoaded >= nativeAdsToLoad) {
            allAdsPreloaded = true;
            runOnUiThread(this::initializeViewPager);
        }
    }

    private void loadNativeAdWithFallback(NativeAdCallback callback) {
        AdRequest adRequest = new AdRequest.Builder().setHttpTimeoutMillis(6000).build();
        AppController.createUnifiedAds(IntroScreensActivity.this, R.string.ADMOB_NATIVE, adRequest, new AdUnifiedListening() {
            @Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                Log.d("NativeAd_Preload", "Normal Native loaded");
                callback.onAdLoaded(nativeAd);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.e("NativeAd_Preload", "Failed Normal Native: " + loadAdError.getMessage());
                callback.onAllAttemptsFailed();
            }
        });
    }

    private void initializeViewPager() {
        hideLoadingProgress();
        binding.lottieView.setVisibility(View.VISIBLE);
        addBottomDots(0);
        changeStatusBarColor();
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        binding.viewPager.setAdapter(myViewPagerAdapter);
        binding.viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    int noOfNativeBetweenIntroSlider = 2;
    private void setupViewPagerLayouts() {
        List<Integer> layoutList = new ArrayList<>();
        introScreenPositions.clear();
        // Always start with first intro screen
        layoutList.add(R.layout.layout_intro_slider1);
        introScreenPositions.add(layoutList.size() - 1);

        // Add native ads and intro screens based on noOfNativeBetweenIntroSlider
        for (int i = 1; i < TOTAL_INTRO_SCREENS; i++) {
            // Add native ad if needed before this intro screen
            if (i <= noOfNativeBetweenIntroSlider) layoutList.add(R.layout.layout_intro_native_ad);
            int introLayoutRes = getResources().getIdentifier("layout_intro_slider" + (i + 1), "layout", getPackageName());
            layoutList.add(introLayoutRes);
            introScreenPositions.add(layoutList.size() - 1);
        }

        // Convert to array
        layouts = new int[layoutList.size()];
        for (int i = 0; i < layoutList.size(); i++) {
            layouts[i] = layoutList.get(i);
        }
    }

    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[TOTAL_INTRO_SCREENS];
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        binding.layoutDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            binding.layoutDots.addView(dots[i]);
        }

        if (dots.length > 0) {
            int introScreenIndex = introScreenPositions.indexOf(currentPage);
            if (introScreenIndex >= 0) {
                dots[introScreenIndex].setTextColor(colorsActive[introScreenIndex]);
            }
        }
    }

    private int getItem(int i) {
        return binding.viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
//        spmCredits.setIntValue("freeCredits",1);
//        spmCredits.setIntValue("credits",1);
        startActivity(new Intent(this, HomeActivity.class)
                .putExtra("isFirstLoad", true));
        finish();
    }

    private final ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            int introScreenIndex = introScreenPositions.indexOf(position);
            if (introScreenIndex >= 0) addBottomDots(position);

            if (position == layouts.length - 1) binding.btnNext.setText("Get Started");
            else binding.btnNext.setText("Next");

            boolean isAdScreen = !introScreenPositions.contains(position);

            if (position==0){
                binding.lottieView.setVisibility(View.VISIBLE);
            }else if (position==1){
                if (isAdScreen) binding.lottieView.setVisibility(View.VISIBLE);
            }
            else{
                binding.lottieView.setVisibility(View.GONE);
            }
            binding.nativeAdViewContainer.setVisibility(isAdScreen ? View.GONE : View.VISIBLE);
            binding.btnNext.setVisibility(isAdScreen ? View.GONE : View.VISIBLE);
            binding.layoutDots.setVisibility(isAdScreen ? View.GONE : View.VISIBLE);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageScrollStateChanged(int state) {}
    };

    private void changeStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater layoutInflater = LayoutInflater.from(IntroScreensActivity.this);
            View view = layoutInflater.inflate(layouts[position], container, false);

            if (!introScreenPositions.contains(position)) {
                // This is a native ad screen
                TemplateView templateView = view.findViewById(R.id.templateView_1);
                if (templateView != null) {
                    int adIndex = getAdIndexForPosition(position);
                    if (adIndex < preloadedNativeAds.size()) {
                        NativeAd nativeAd = preloadedNativeAds.get(adIndex);
                        showNativeAd(nativeAd, templateView);
                    } else {
                        templateView.setVisibility(View.GONE);
                    }
                }
            }

            container.addView(view);
            return view;
        }

        private int getAdIndexForPosition(int position) {
            int adCount = 0;
            for (int i = 0; i < position; i++) {
                if (!introScreenPositions.contains(i)) adCount++;
            }
            return adCount;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    private void showNativeAd(NativeAd nativeAd, TemplateView templateView) {
        NativeTemplateStyle style = new NativeTemplateStyle.Builder().build();
        templateView.setStyles(style);
        templateView.setNativeAd(nativeAd);
        templateView.setVisibility(View.VISIBLE);
    }

    private interface NativeAdCallback {
        void onAdLoaded(NativeAd nativeAd);
        void onAllAttemptsFailed();
    }

    @Override
    protected void onDestroy() {
        // Clean up native ads when activity is destroyed
        for (NativeAd ad : preloadedNativeAds) {
            if (ad != null) ad.destroy();
        }
        preloadedNativeAds.clear();
        super.onDestroy();
    }
}