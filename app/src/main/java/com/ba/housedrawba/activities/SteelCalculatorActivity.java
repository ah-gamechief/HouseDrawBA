package com.ba.housedrawba.activities;
import static com.ba.housedrawba.adspackage.AppController.adClickCounter;
import static com.ba.housedrawba.adspackage.AppController.adsShowInterval;
import static com.ba.housedrawba.adspackage.AppController.createAndGetAdaptiveAd;
import static com.ba.housedrawba.adspackage.AppController.showInterstitialAdWithLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.R;
import com.ba.housedrawba.databinding.ActivitySteelCalculatorBinding;

import java.text.DecimalFormat;
public class SteelCalculatorActivity extends BaseActivity {
    ActivitySteelCalculatorBinding binding;
    Activity activity = this;
    Context context = this;

    String[] spinnerList = { "8 mm", "10 mm", "12 mm","16 mm","20 mm","25 mm","32 mm","36 mm"};
    int diameter = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySteelCalculatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createAndGetAdaptiveAd(activity,binding.adViewContainer);

        if (adClickCounter>0 && adClickCounter%adsShowInterval==0)
            showInterstitialAdWithLoader(activity,null);

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
                        diameter = 8;
                        break;
                    case 1:
                        diameter = 10;
                        break;
                    case 2:
                        diameter = 12;
                        break;
                    case 3:
                        diameter = 16;
                        break;
                    case 4:
                        diameter = 20;
                        break;
                    case 5:
                        diameter = 25;
                        break;
                    case 6:
                        diameter = 32;
                        break;
                    case 7:
                        diameter = 36;
                        break;

                }
            }
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        binding.etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.etBars.setText("1");
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        findViewById(R.id.calculateBtn).setOnClickListener(view -> {
            String length   = String.valueOf(binding.etLength.getText());
            String price    = String.valueOf(binding.etPrice.getText());
            String bars     = String.valueOf(binding.etBars.getText());

            if (length.isEmpty()){
                binding.etLength.setError("Required");
            }else if (price.isEmpty()){
                binding.etPrice.setError("Required");
            }
            else {
                if (bars.isEmpty()) bars = "1";

                double kgPerFeet = diameter*diameter/533f;
                double netKgs = kgPerFeet*Integer.parseInt(length)*Integer.parseInt(bars);
                double netPrice = Float.parseFloat(price)*netKgs;

                DecimalFormat decimalFormat = new DecimalFormat("0.00");
//                double netPrice = calculateSteel(length, diameter, price, quantity);
//                calculationBinding.tvResult.setText("Result: " + result);
//                float finalValue = length*width*height*brickSize;
//                float areaTotal = length*width*height;
                startActivity(new Intent(activity,CalculationResultActivity.class)
                        .putExtra("type",2)
                        .putExtra("netWeight",decimalFormat.format(netKgs)+" Kg")
                        .putExtra("netPrice","Rs. "+decimalFormat.format(netPrice))
                        .putExtra("bars",bars)
                );
            }
        });

    }

    public double calculateSteel(String length, String diameter, String price, String quantity) {
        try {
            double lengthValue = Double.parseDouble(length);
            double diameterValue = Double.parseDouble(diameter);
            double priceValue = Double.parseDouble(price);
            double quantityValue = Double.parseDouble(quantity);

            double diameterSquared = diameterValue * diameterValue;
            double finalDiameter = diameterSquared / 533;

            double result = lengthValue * finalDiameter;
            double getPrice = priceValue * result;
            double finalResult = getPrice  * quantityValue;

            return finalResult;
        } catch (NumberFormatException e) {

            throw new IllegalArgumentException("Invalid input");
        }
    }
}