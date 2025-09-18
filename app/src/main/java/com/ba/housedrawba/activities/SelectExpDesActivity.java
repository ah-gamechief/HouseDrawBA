package com.ba.housedrawba.activities;
import static com.ba.housedrawba.adspackage.AppController.adClickCounter;
import static com.ba.housedrawba.adspackage.AppController.adsShowInterval;
import static com.ba.housedrawba.adspackage.AppController.createAndGetAdaptiveAd;
import static com.ba.housedrawba.adspackage.AppController.showInterstitialAdWithLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.adspackage.AppController;
import com.ba.housedrawba.databinding.ActivitySelectExpDesBinding;
public class SelectExpDesActivity extends BaseActivity {
    ActivitySelectExpDesBinding binding;
    Activity activity = this;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectExpDesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createAndGetAdaptiveAd(activity,binding.adViewContainer);
        if (adClickCounter>0 && adClickCounter%adsShowInterval==0)
            showInterstitialAdWithLoader(activity, new AppController.InterstitialCustomAdListener() {
                @Override
                public void onAdClosedListener() {

                }

                @Override
                public void onAdFailedToLoad(String errorMsg) {

                }
            });

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        binding.button1.setOnClickListener(view -> {
            triggerFirebaseInAppEvent(activity,"ExploreActivity_2DPlans");
            SelectExpDesActivity.this.startActivity(new Intent(context, ExploreDesignsActivity.class)
                    .putExtra("categoryType", 1));
        });
        binding.button2.setOnClickListener(view -> {
            triggerFirebaseInAppEvent(activity,"ExploreActivity_3DPlans");
            SelectExpDesActivity.this.startActivity(new Intent(context, ExploreDesignsActivity.class)
                    .putExtra("categoryType", 2));
        });
    }
}