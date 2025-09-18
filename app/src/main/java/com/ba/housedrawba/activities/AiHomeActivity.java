package com.ba.housedrawba.activities;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.R;
import com.ba.housedrawba.Utils.CacheUtils;
import com.ba.housedrawba.adapters.AdapterSavedAiResults;
import com.ba.housedrawba.databinding.ActivityAiHomeBinding;
import com.ba.housedrawba.interfaces.OnRvItemClickListener;

import java.util.List;
public class AiHomeActivity extends BaseActivity {
    ActivityAiHomeBinding binding;
    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAiHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
    }
    private void initViews() {
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        String videoPath = "android.resource://" + activity.getPackageName() + "/" + R.raw.vid_ai_1;
        Uri videoUri = Uri.parse(videoPath);
        binding.videoView.setVideoURI(videoUri);
        binding.videoView.start();
        binding.videoView.setOnCompletionListener(mp -> {
            // When one video completes, play the next one
            binding.videoView.start();
        });
        binding.videoView.setOnPreparedListener(MediaPlayer::start);

        binding.button1.setOnClickListener(v -> startActivity(new Intent(activity, AiGetInputActivity.class)));

    }

    @Override
    protected void onResume() {
        super.onResume();
        funLoadRvData();
    }

    private void funLoadRvData(){
        List<CacheUtils.BitmapPair> allBitmapPairs = CacheUtils.getAllBitmapPairs(activity);

        if (allBitmapPairs.isEmpty()){
            binding.recyclerView.setVisibility(View.GONE);
            binding.btnEmpty.setVisibility(View.VISIBLE);
        }else{
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.btnEmpty.setVisibility(View.GONE);
            AdapterSavedAiResults adapterSavedAiResults = new AdapterSavedAiResults(allBitmapPairs, new OnRvItemClickListener() {
                @Override
                public void onItemClicked(int po) {
                    if (allBitmapPairs.get(po)!=null){
                        String path1st = allBitmapPairs.get(po).pathWith;
                        String path2nd = allBitmapPairs.get(po).pathWithout;
//                    Intent intent = new Intent(activity, AiResultPreviewActivity.class);
//                    intent.putExtra("inputImgUri",path2nd);
//                    intent.putExtra("outputImgUri",path1st);
//                    startActivity(intent);
                        Log.d("AiResult__1", "position: " + path1st + " - "+path2nd);
                    }
                }
            });
            binding.recyclerView.setAdapter(adapterSavedAiResults);
        }

    }
}