package com.ba.housedrawba.interfaces;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.ba.housedrawba.R;
import com.ba.housedrawba.adspackage.AppController;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public interface OnRvItemClickListener {
    void onItemClicked(int position);
}