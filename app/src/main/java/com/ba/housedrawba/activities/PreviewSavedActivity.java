package com.ba.housedrawba.activities;

import static com.ba.housedrawba.adspackage.AppController.createAndGetAdaptiveAd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.databinding.ActivityPreviewSavedBinding;
import com.bumptech.glide.Glide;
public class PreviewSavedActivity extends BaseActivity {
    Activity activity = this;
    Context context = this;
    String mSaveImageUri;
    ActivityPreviewSavedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSaveImageUri = getIntent().getStringExtra("asset_path");
        binding = ActivityPreviewSavedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createAndGetAdaptiveAd(activity,binding.adViewContainer);

//        if (adClickCounter>0 && adClickCounter%adsShowInterval==0) showInterstitialAdWithLoader(activity,null);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        if (mSaveImageUri!=null){
            System.out.println("iiiiiiiiiiiiiiiiiiiirt1-="+mSaveImageUri);
            Glide.with(context).load(mSaveImageUri).into(binding.imageView);
            binding.tvImagePath.setText(mSaveImageUri);
        }

        binding.btnFinish.setOnClickListener(view -> {
            startActivity(new Intent(activity, HomeActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        });
    }
}