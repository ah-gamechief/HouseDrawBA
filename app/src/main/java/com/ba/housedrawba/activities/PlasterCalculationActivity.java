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
import com.ba.housedrawba.databinding.ActivityPlasterCalculationBinding;

import java.text.DecimalFormat;
public class PlasterCalculationActivity extends BaseActivity {
    ActivityPlasterCalculationBinding binding;
    Activity activity = this;
    Context context = this;

    String[] spinnerList = {"12 mm","15 mm","20 mm"};
    String[] spinnerList2 = {"C:M 1:3","C:M 1:4","C:M 1:6"};
    int cementThickness = 12;
    int mortarRatio = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlasterCalculationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createAndGetAdaptiveAd(activity,binding.adViewContainer);

        if (adClickCounter>0 && adClickCounter%adsShowInterval==0)
            showInterstitialAdWithLoader(activity,null);

        binding.spinner.setSelection(1);
        binding.spinner2.setSelection(1);
        ArrayAdapter aa = new ArrayAdapter(context,android.R.layout.simple_spinner_item,spinnerList);
        ArrayAdapter aa2 = new ArrayAdapter(context,android.R.layout.simple_spinner_item,spinnerList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        binding.spinner.setAdapter(aa);
        binding.spinner2.setAdapter(aa2);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                    default:
                        cementThickness = 12;
                        break;
                    case 1:
                        cementThickness = 15;
                        break;
                    case 2:
                        cementThickness = 0;
                        break;
                }
            }
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        binding.spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                    default:
                        mortarRatio = 3;
                        break;
                    case 1:
                        mortarRatio = 4;
                        break;
                    case 2:
                        mortarRatio = 6;
                        break;
                }
            }
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        binding.calculateBtn.setOnClickListener(view -> {
            String width   = String.valueOf(binding.etWidth.getText());
            String height   = String.valueOf(binding.etWidth.getText());
            String cementPrice    = String.valueOf(binding.etCementPrice.getText());
            String sandPrice    = String.valueOf(binding.etSandPrice.getText());

            if (width.isEmpty()){
                binding.etWidth.setError("Required");
            }else if (height.isEmpty()){
                binding.etHeight.setError("Required");
            }else if (cementPrice.isEmpty()){
                binding.etHeight.setError("Required");
            }else if (sandPrice.isEmpty()){
                binding.etSandPrice.setError("Required");
            }
            else {
                double plasterArea = calculatePlasterArea(width,height, String.valueOf(cementThickness));

                DecimalFormat decimalFormat = new DecimalFormat("0.00");
//                double netPrice = calculateSteel(length, diameter, price, quantity);
//                calculationBinding.tvResult.setText("Result: " + result);
//                float finalValue = length*width*height*brickSize;
//                float areaTotal = length*width*height;
                startActivity(new Intent(activity,CalculationResultActivity.class)
                        .putExtra("type",4)
                        .putExtra("netWeight",decimalFormat.format(plasterArea)+"")
//                        .putExtra("netPrice","Rs. "+decimalFormat.format(netPrice))
//                        .putExtra("bars",bars)
                );
            }
        });
    }

    public double calculatePlasterArea(String length, String diameter, String thick) {
        try {
            double lengthValue = Double.parseDouble ( length );
            double widthValue = Double.parseDouble ( diameter );
            double thickness = Double.parseDouble ( thick );

            double PlasterArea = (lengthValue) * (widthValue) * 10.764;
            double Thickness = PlasterArea * thickness;

            return PlasterArea;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException ( "Invalid input" );
        }
    }

    public double calculatePaintLitere(String length, String diameter, String thick, String cement, String Sand) {
        try {
            double lengthValue = Double.parseDouble ( length );
            double widthValue = Double.parseDouble ( diameter );
            double thickness = Double.parseDouble ( thick );
            double sand = Double.parseDouble ( Sand );
            double cemnt = Double.parseDouble ( cement );
            double sumof_SC = sand + cemnt;

            double PlasterArea = (lengthValue) * (widthValue) * 10.764;
            double thikinMeter = thickness * 0.012;
            double Thickness = PlasterArea * thikinMeter;

            double cementRequire =(Thickness * cemnt)/sumof_SC;
            double cementRequireFinal = cementRequire * 1000;

            return cementRequireFinal;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException ( "Invalid input" );
        }
    }

    public double calculatePaintPrice(String length, String diameter, String thick, String cement, String Sand) {
        try {
            double lengthValue = Double.parseDouble ( length );
            double widthValue = Double.parseDouble ( diameter );
            double thickness = Double.parseDouble ( thick );
            double sand = Double.parseDouble ( Sand );
            double cemnt = Double.parseDouble ( cement );

            double sumof_SC = sand + cemnt;

            double PlasterArea = (lengthValue) * (widthValue) * 10.764;
            double thikinMeter = thickness * 0.012;
            double Thickness = PlasterArea * thikinMeter;

            double SandRequire =(Thickness * sand)/sumof_SC;
            double SandRequireFinal = SandRequire * 1000;

            return SandRequireFinal;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException ( "Invalid input" );
        }
    }


}