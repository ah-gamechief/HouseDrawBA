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
import com.ba.housedrawba.databinding.ActivityTilesCalculatorBinding;

import java.text.DecimalFormat;
public class TilesCalculatorActivity extends BaseActivity {
    ActivityTilesCalculatorBinding binding;
    Activity activity = this;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTilesCalculatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createAndGetAdaptiveAd(activity,binding.adViewContainer);

        if (adClickCounter>0 && adClickCounter%adsShowInterval==0)
            showInterstitialAdWithLoader(activity,null);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());

        binding.calculateBtn.setOnClickListener(view -> {
            String width  = String.valueOf(binding.etWidth.getText());
            String height = String.valueOf(binding.etHeight.getText());
            String price  = String.valueOf(binding.etPrice.getText());

            if (width.isEmpty()){
                binding.etWidth.setError("Required");
            }else if (height.isEmpty()){
                binding.etHeight.setError("Required");
            }else if (price.isEmpty()){
                binding.etPrice.setError("Required");
            }
            else {
//                double result = calculateTiles ( length, diameter, price, quantity );
                double totalArea = Double.parseDouble(width)*Double.parseDouble(height);
                double totalPrice = Double.parseDouble(price)*totalArea;
                DecimalFormat decimal = new DecimalFormat("0.00");
                DecimalFormat decimal2 = new DecimalFormat("0");

//                float finalValue = length*width*height*brickSize;
//                float areaTotal = length*width*height;
                startActivity(new Intent(activity,CalculationResultActivity.class)
                        .putExtra("type",5)
                        .putExtra("value",decimal2.format(totalPrice)+"")
                        .putExtra("areaTotal",decimal.format(totalArea)+" sqrft")
                );
            }
        });
    }

    public double calculateTiles(String length, String diameter, String price, String quantity) {
        try {
            double lengthValue = Double.parseDouble ( length );
            double widthValue = Double.parseDouble ( diameter );
            double tilelength = Double.parseDouble ( price );
            double tilelwidth = Double.parseDouble ( quantity );

            double floorArea = lengthValue * widthValue;
            double areaTile = (tilelength ) * (tilelwidth );

            int finalareaTile = (int) Math.floor(floorArea / areaTile);

            return finalareaTile;
        } catch (NumberFormatException e) {

            throw new IllegalArgumentException ( "Invalid input" );
        }
    }
}