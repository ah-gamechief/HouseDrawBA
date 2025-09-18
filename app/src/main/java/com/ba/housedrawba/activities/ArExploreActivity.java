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
import com.ba.housedrawba.R;
import com.ba.housedrawba.adapters.Adapter_ARModels;
import com.ba.housedrawba.adspackage.AppController;
import com.ba.housedrawba.databinding.ActivityArExploreBinding;
public class ArExploreActivity extends BaseActivity {
    ActivityArExploreBinding binding;
    Activity activity = this;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArExploreBinding.inflate(getLayoutInflater());
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


        int[] dataList = {
                R.drawable.floor_1,
                R.drawable.floor_2,
                R.drawable.floor_3,
                R.drawable.floor_4,
                R.drawable.floor_5,
        };

        Adapter_ARModels adapterArModels = new Adapter_ARModels(dataList, position -> {
            if (!checkCameraAppPermission(activity)) {
                requestCameraPermission(activity, () -> {
                    adClickCounter++;
                    startActivity(new Intent(activity, ArDrawCameraActivity.class)
                            .putExtra("image", dataList[position]));
                });
            }else{
                adClickCounter++;
                startActivity(new Intent(activity, ArDrawCameraActivity.class)
                        .putExtra("image", dataList[position]));
            }
        });
        binding.recyclerView.setAdapter(adapterArModels);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 977) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK
                if (data != null) {
                    adClickCounter++;
                    int retDat = data.getIntExtra("result",R.drawable.fp_1);
                    startActivity(new Intent(activity, ArDrawCameraActivity.class)
                            .putExtra("image", retDat));
                }
//                showToast("Permission Granted. You may proceed now.");
            }
        }
    }
}