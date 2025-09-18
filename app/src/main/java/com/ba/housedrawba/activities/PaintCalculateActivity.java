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
import com.ba.housedrawba.databinding.ActivityPaintCalculateBinding;

import java.text.DecimalFormat;
public class PaintCalculateActivity extends BaseActivity {
    ActivityPaintCalculateBinding binding;
    Activity activity = this;
    Context context = this;
    float littersIn1SqrFeet = 0.019f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaintCalculateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createAndGetAdaptiveAd(activity,binding.adViewContainer);

        if (adClickCounter>0 && adClickCounter%adsShowInterval==0)
            showInterstitialAdWithLoader(activity,null);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());

        binding.calculateBtn.setOnClickListener(view -> {
            String mWidth  = String.valueOf(binding.etWidth.getText());
            String mHeight = String.valueOf(binding.etHeight.getText());
            String mCoats  = String.valueOf(binding.etCoats.getText());
            String mPrice  = String.valueOf(binding.etPrice.getText());

            if (mWidth.isEmpty()){
                binding.etWidth.setError("Required");
            }else if (mHeight.isEmpty()){
                binding.etHeight.setError("Required");
            }else if (mCoats.isEmpty()){
                binding.etCoats.setError("Required");
            }else if (mPrice.isEmpty()){
                binding.etPrice.setError("Required");
            }
            else {
                double calArea = Double.parseDouble(mWidth)*Double.parseDouble(mHeight);
                double calPaint = calArea*littersIn1SqrFeet;
                double netPaint = Integer.parseInt(mCoats)*calPaint;
                double netPrice = Double.parseDouble(mPrice)*netPaint;

                DecimalFormat precision = new DecimalFormat("0.00");
                startActivity(new Intent(activity,CalculationResultActivity.class)
                        .putExtra("type",3)
                        .putExtra("price",""+precision.format(netPrice))
                        .putExtra("paint",precision.format(netPaint)+"Litter")
                        .putExtra("areaTotal",""+precision.format(calArea))
                );
            }
        });
    }

    public double calculatePaintArea(String length, String diameter, String subtract, String perlitere, String coats, String price1) {
        try {
            double lengthValue = Double.parseDouble ( length );
            double widthValue = Double.parseDouble ( diameter );
            double subtractarea = Double.parseDouble ( subtract );
            double perLitere = Double.parseDouble ( perlitere );
            double no_coats = Double.parseDouble ( coats );
            double price = Double.parseDouble ( price1 );

            double WallArea = (lengthValue) * (widthValue);
            double subtractArea = WallArea - subtractarea;
            double perLitere0 = subtractArea / perLitere;
            double finalresult = perLitere0 * no_coats;
            double FinalPrice = finalresult * price;


           /* double diameterSquared = diameterValue * diameterValue;
            double finalDiameter = diameterSquared / 533;

            double result = lengthValue * finalDiameter;
            double getPrice = priceValue * result;
            double finalResult = getPrice  * quantityValue;

            return finalResult;*/

            return subtractArea;
        } catch (NumberFormatException e) {

            throw new IllegalArgumentException ( "Invalid input" );
        }
    }

    public double calculatePaintLitere(String length, String diameter, String subtract, String perlitere, String coats, String price1) {
        try {
            double lengthValue = Double.parseDouble ( length );
            double widthValue = Double.parseDouble ( diameter );
            double subtractarea = Double.parseDouble ( subtract );
            double perLitere = Double.parseDouble ( perlitere );
            double no_coats = Double.parseDouble ( coats );
            double price = Double.parseDouble ( price1 );

            double WallArea = (lengthValue) * (widthValue);
            double subtractArea = WallArea - subtractarea;

            double perLitere0 = subtractArea / perLitere;

            double finalresult = perLitere0 * no_coats;

            double FinalPrice = finalresult * price;

           /* double diameterSquared = diameterValue * diameterValue;
            double finalDiameter = diameterSquared / 533;
            double result = lengthValue * finalDiameter;
            double getPrice = priceValue * result;
            double finalResult = getPrice  * quantityValue;

            return finalResult;*/

            return finalresult;
        } catch (NumberFormatException e) {

            throw new IllegalArgumentException ( "Invalid input" );
        }

    }

    public double calculatePaintPrice(String length, String diameter, String subtract, String perlitere, String coats, String price1) {
        try {
            double lengthValue = Double.parseDouble ( length );
            double widthValue = Double.parseDouble ( diameter );
            double subtractarea = Double.parseDouble ( subtract );
            double perLitere = Double.parseDouble ( perlitere );
            double no_coats = Double.parseDouble ( coats );

            double price = Double.parseDouble ( price1 );

            double WallArea = (lengthValue) * (widthValue);
            double subtractArea = WallArea - subtractarea;

            double perLitere0 = subtractArea / perLitere;

            double finalresult = perLitere0 * no_coats;
            double FinalPrice = finalresult * price;

           /* double diameterSquared = diameterValue * diameterValue;
            double finalDiameter = diameterSquared / 533;
            double result = lengthValue * finalDiameter;
            double getPrice = priceValue * result;
            double finalResult = getPrice  * quantityValue;

            return finalResult;*/

            return FinalPrice;
        } catch (NumberFormatException e) {

            throw new IllegalArgumentException ( "Invalid input" );
        }
    }
}