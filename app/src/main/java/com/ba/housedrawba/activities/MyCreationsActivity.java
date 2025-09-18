package com.ba.housedrawba.activities;

import static com.ba.housedrawba.adspackage.AppController.adClickCounter;
import static com.ba.housedrawba.adspackage.AppController.adsShowInterval;
import static com.ba.housedrawba.adspackage.AppController.createAndGetAdaptiveAd;
import static com.ba.housedrawba.adspackage.AppController.isInAppPurchased;
import static com.ba.housedrawba.adspackage.AppController.showInterstitialAdWithLoader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.R;
import com.ba.housedrawba.adapters.Adapter_SavedImages;
import com.ba.housedrawba.databinding.ActivityMyCreationsBinding;
import com.ba.housedrawba.models.SavedImagesModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class MyCreationsActivity extends BaseActivity {
    ActivityMyCreationsBinding binding;
    Adapter_SavedImages adapter;
    List<SavedImagesModel> dataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyCreationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createAndGetAdaptiveAd(this,binding.adViewContainer);

        if (adClickCounter>0 && adClickCounter%adsShowInterval==0)
            showInterstitialAdWithLoader(this,null);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadSavedImages();
    }

    public void loadSavedImages() {
        dataList = new ArrayList<>();
        String desFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        String parentFolder = getResources().getString(R.string.folder_to_save_imgs);

        String path = desFolder+"/"+parentFolder;

        File directory = new File(path);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                Log.d("Files", "Size: " + files.length);
                dataList.clear();
                int i=0;
                for (File file : files) {
                    i=i+1;
                    Log.d("Files", "FileName:" + file.getName());
                    Log.d("Files", "FilePath:=" + path+"/"+file.getName());
                    String imgUri = path+"/"+file.getName();
                    if (!file.getName().equals("uploads")) {
                        SavedImagesModel savedModel = new SavedImagesModel(String.valueOf(i),imgUri,false);
                        dataList.add(savedModel);
                    }
                }
                if (dataList.isEmpty()){
                    binding.emptyTy.setVisibility(View.VISIBLE);
//                    binding.centerBannerAdView.setVisibility(View.VISIBLE);
                    binding.rvSavedImages.setVisibility(View.GONE);
                    if (!isInAppPurchased) {
                        loadAdIfListEmpty();
                    }else {
//                        binding.centerBannerAdView.setVisibility(View.GONE);
                    }
                }else {
                    binding.emptyTy.setVisibility(View.GONE);
//                    binding.centerBannerAdView.setVisibility(View.GONE);
                    binding.rvSavedImages.setVisibility(View.VISIBLE);
                    adapter = new Adapter_SavedImages(this, dataList,
                            binding.layoutDelSaved,binding.btnCancelSaved,binding.btnDelSaved,binding.checkboxSaved);
                    binding.rvSavedImages.setAdapter(adapter);
                }
            }else{
                binding.emptyTy.setVisibility(View.VISIBLE);
                binding.rvSavedImages.setVisibility(View.GONE);
//                binding.centerBannerAdView.setVisibility(View.VISIBLE);
                if (!isInAppPurchased) {
                    loadAdIfListEmpty();
                }else {
//                    binding.centerBannerAdView.setVisibility(View.GONE);
                }
            }
        }else{
            binding.emptyTy.setVisibility(View.VISIBLE);
            binding.rvSavedImages.setVisibility(View.GONE);
//            binding.centerBannerAdView.setVisibility(View.VISIBLE);
            if (!isInAppPurchased) {
                loadAdIfListEmpty();
            }else {
//                binding.centerBannerAdView.setVisibility(View.GONE);
            }
        }
    }
    public void showDelLayout(Activity activity){
        if (binding==null) binding = ActivityMyCreationsBinding.inflate(activity.getLayoutInflater());
        binding.layoutDelSaved.setVisibility(View.VISIBLE);

    }
    public void hideDelLayout(Activity activity){
        if (binding==null) binding = ActivityMyCreationsBinding.inflate(activity.getLayoutInflater());
        binding.layoutDelSaved.setVisibility(View.GONE);
    }

    public void isListEmpty(Activity activity){
        if (binding==null) binding = ActivityMyCreationsBinding.inflate(activity.getLayoutInflater());
        binding.emptyTy.setVisibility(View.VISIBLE);
//        binding.centerBannerAdView.setVisibility(View.VISIBLE);
        binding.rvSavedImages.setVisibility(View.GONE);
        if (!isInAppPurchased) {
            loadAdIfListEmpty();
        }else {
//            binding.centerBannerAdView.setVisibility(View.GONE);
        }
    }

    private void loadAdIfListEmpty(){
        /*AdRequest adRequest2 = new AdRequest.Builder().build();
        binding.centerBannerAdView.loadAd(adRequest2);

        binding.centerBannerAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                binding.centerBannerAdView.setVisibility(View.GONE);
            }
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        });*/
    }
}