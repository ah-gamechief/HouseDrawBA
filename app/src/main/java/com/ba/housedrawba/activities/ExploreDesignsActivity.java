package com.ba.housedrawba.activities;
import static com.ba.housedrawba.adspackage.AppController.adClickCounter;
import static com.ba.housedrawba.adspackage.AppController.adsShowInterval;
import static com.ba.housedrawba.adspackage.AppController.createAndGetAdaptiveAd;
import static com.ba.housedrawba.adspackage.AppController.showInterstitialAdWithLoader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ba.housedrawba.adapters.Adapter_RvExplore;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.databinding.ActivityExploreDesignsBinding;

import java.util.ArrayList;
public class ExploreDesignsActivity extends BaseActivity {
    ActivityExploreDesignsBinding binding;
    Activity activity = this;
    Context context  = this;
    ArrayList<String> dataList;
    int categoryType;
    int clickCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExploreDesignsBinding.inflate(getLayoutInflater());
        categoryType = getIntent().getIntExtra("categoryType",1);
        setContentView(binding.getRoot());

        createAndGetAdaptiveAd(activity,binding.adViewContainer);

        if (adClickCounter>0 && adClickCounter%adsShowInterval==0)
            showInterstitialAdWithLoader(activity,null);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        switch (categoryType) {
            case 1:
            default:
                dataList = getHomeScreenDataList(activity,"2d");
                break;
            case 2:
                dataList = getHomeScreenDataList(activity,"3d");
                break;
            case 3:
                dataList = getHomeScreenDataList(activity,"interior");
                break;
            case 4:
                dataList = getHomeScreenDataList(activity,"exterior");
                break;
            case 5:
                dataList = getHomeScreenDataList(activity,"portable");
                break;
            case 6:
                dataList = getHomeScreenDataList(activity,"room");
                break;
            case 7:
                dataList = getHomeScreenDataList(activity,"bath");
                break;
            case 8:
                dataList = getHomeScreenDataList(activity,"office");
                break;
        }

        if (!dataList.isEmpty()){
            Glide.with(binding.zoomImageView).asBitmap().load(dataList.get(0)).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    binding.zoomImageView.setImageBitmap(resource);
                }@Override public void onLoadCleared(@Nullable Drawable placeholder) {}});
        }


        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.recyclerView.setLayoutManager(layoutManager);

        Adapter_RvExplore adapter = new Adapter_RvExplore(dataList, po -> {
            if (binding.recyclerView.getAdapter()!=null)
                ((Adapter_RvExplore) binding.recyclerView.getAdapter()).setSelected(po,true);

            if (dataList.get(po)!=null) {

                scrollToCenter(layoutManager,binding.recyclerView,po);
                Glide.with(binding.zoomImageView).asBitmap().load(dataList.get(po)).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        binding.zoomImageView.setImageBitmap(resource);
                    }@Override public void onLoadCleared(@Nullable Drawable placeholder) {}});
            }

            /*if (clickCounter>0 && clickCounter%5==0)
                showInterstitialAdWithLoader(activity,null);
            clickCounter++;*/
        });
        binding.recyclerView.setAdapter(adapter);
    }
}