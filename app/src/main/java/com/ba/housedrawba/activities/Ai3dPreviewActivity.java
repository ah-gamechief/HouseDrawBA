package com.ba.housedrawba.activities;
import android.os.Bundle;
import android.util.Log;

import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.databinding.ActivityAi3dPreviewBinding;
import com.bc.aifloorplansbc.utils.ModelViewer;
import com.google.android.filament.Skybox;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
public class Ai3dPreviewActivity extends BaseActivity {
    ActivityAi3dPreviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAi3dPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
    }
    private void initViews(){
        UltimateBarX.statusBarOnly(this)
                .fitWindow(true)
                .transparent()
                .colorRes(android.R.color.transparent)
                .light(false)
                .apply();
        binding.closeBtn.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.sceneView.setEnvironment(null);

        String model3dUri = getIntent().getStringExtra("outputImgUri");
        if (model3dUri != null)
            ModelViewer.setup(binding.sceneView, model3dUri, 1.0f, true, 45f, 45f, 3f);
        else showToast("Model not found it's null");

        Skybox skybox = new Skybox.Builder()
                .color(0.53f, 0.81f, 0.92f, 1.0f)   // Sky blue
                .build(binding.sceneView.getEngine());
        binding.sceneView.getScene().setSkybox(skybox);

        // Reset with smooth animation
        binding.resetBtn.setOnClickListener(v -> {
            Log.d("resetBtn_nnnn0", "resetBtn");
            ModelViewer.resetCamera(true, 1000); // animate over 1 second
        });
    }
}