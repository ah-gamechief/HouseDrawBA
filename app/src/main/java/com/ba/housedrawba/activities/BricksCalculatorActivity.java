package com.ba.housedrawba.activities;
import static com.ba.housedrawba.adspackage.AppController.adClickCounter;
import static com.ba.housedrawba.adspackage.AppController.adsShowInterval;
import static com.ba.housedrawba.adspackage.AppController.createAndGetAdaptiveAd;
import static com.ba.housedrawba.adspackage.AppController.showInterstitialAdWithLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.R;
import com.ba.housedrawba.adspackage.AppController;
import com.ba.housedrawba.databinding.ActivityBrickesCalculatorBinding;
public class BricksCalculatorActivity extends BaseActivity {
    ActivityBrickesCalculatorBinding binding;
    Activity activity = this;
    Context context = this;

    String[] spinnerList = { "Half Brick", "1 Brick", "2 Bricks"};
    int brickSize = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBrickesCalculatorBinding.inflate(getLayoutInflater());
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
        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());

        binding.spinner.setSelection(1);
        ArrayAdapter aa = new ArrayAdapter(context,android.R.layout.simple_spinner_item,spinnerList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        binding.spinner.setAdapter(aa);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                    default:
                        brickSize = 7;
                        break;
                    case 1:
                        brickSize = 14;
                        break;
                    case 2:
                        brickSize = 21;
                        break;
                }
            }
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        findViewById(R.id.calculateBtn).setOnClickListener(view -> {
            if (binding.etLength.getText().toString().isEmpty()){
                binding.etLength.setError("Required");
            }else if (binding.etWidth.getText().toString().isEmpty()){
                binding.etWidth.setError("Required");
            }else if (binding.etHeight.getText().toString().isEmpty()){
                binding.etHeight.setError("Required");
            }else {
                float length = Float.parseFloat(binding.etLength.getText().toString());
                float width = Float.parseFloat(binding.etWidth.getText().toString());
                float height = Float.parseFloat(binding.etHeight.getText().toString());

                float finalValue = length*width*height*brickSize;
                float areaTotal = length*width*height;

                startActivity(new Intent(activity,CalculationResultActivity.class)
                        .putExtra("type",1)
                        .putExtra("value",finalValue+"")
                        .putExtra("areaTotal",areaTotal+"")
                );


            }
        });
    }
}