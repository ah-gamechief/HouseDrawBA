package com.ba.housedrawba.activities;
import static com.ba.housedrawba.adspackage.AppController.adClickCounter;
import static com.ba.housedrawba.adspackage.AppController.adsShowInterval;
import static com.ba.housedrawba.adspackage.AppController.createAndGetAdaptiveAd;
import static com.ba.housedrawba.adspackage.AppController.showInterstitialAdWithLoader;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.R;
import com.ba.housedrawba.databinding.ActivityCalculationResultBinding;
public class CalculationResultActivity extends BaseActivity {
    ActivityCalculationResultBinding binding;
    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String value = getIntent().getStringExtra("value");
        String areaTotal = getIntent().getStringExtra("areaTotal");
        int type = getIntent().getIntExtra("type",1);

        String price = getIntent().getStringExtra("price");
        String paint = getIntent().getStringExtra("paint");

        String netWeight = getIntent().getStringExtra("netWeight");
        String netPrice  = getIntent().getStringExtra("netPrice");
        String bars  = getIntent().getStringExtra("bars");

        binding = ActivityCalculationResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createAndGetAdaptiveAd(activity,binding.adViewContainer);

        if (adClickCounter>0 && adClickCounter%adsShowInterval==0)
            showInterstitialAdWithLoader(activity,null);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());

        switch (type){
            case 1:
            default:
                binding.ivRoom2.setImageResource(R.drawable.dsh_bricks_icon);
                if (value!=null)
                    binding.tvValue1.setText(value + "");
                if (areaTotal!=null)
                    binding.tvValue2.setText(areaTotal + " ft");

                binding.tvNone3.setVisibility(View.GONE);
                binding.tvValue3.setVisibility(View.GONE);
                binding.lineView2.setVisibility(View.GONE);
                break;
            case 2:
                binding.ivRoom2.setImageResource(R.drawable.dsh_steel_icon);
                binding.tvNone3.setVisibility(View.VISIBLE);
                binding.tvValue3.setVisibility(View.VISIBLE);
                binding.lineView2.setVisibility(View.VISIBLE);

                if (netWeight!=null){
                    binding.tvNone1.setText("Total Weight: ");
                    binding.tvValue1.setText(netWeight);
                }
                if (netPrice!=null){
                    binding.tvNone2.setText("Total Price: ");
                    binding.tvValue2.setText(netPrice);
                }
                if (bars!=null){
                    binding.tvNone3.setText("Total Bars: ");
                    binding.tvValue3.setText(""+bars);
                }

                break;
            case 3:
                binding.ivRoom2.setImageResource(R.drawable.dsh_paint_icon);
                if (price!=null && paint!=null){
                    binding.tvNone1.setText("Total Paint Required: ");
                    binding.tvValue1.setText(paint+"");
                    binding.tvNone2.setText("Total Area: ");
                    binding.tvValue2.setText(areaTotal+" Sqrft");
                    binding.tvNone3.setText("Total Estimated Price: ");
                    binding.tvValue3.setText(price+"");

                    binding.tvNone3.setVisibility(View.VISIBLE);
                    binding.tvValue3.setVisibility(View.VISIBLE);
                    binding.lineView2.setVisibility(View.VISIBLE);
                }
                break;
            case 4:
                binding.ivRoom2.setImageResource(R.drawable.dsh_plaster_icon);
                break;
            case 5:
                binding.ivRoom2.setImageResource(R.drawable.dsh_tiles_icon);
                binding.tvNone3.setVisibility(View.GONE);
                binding.tvValue3.setVisibility(View.GONE);
                binding.lineView2.setVisibility(View.GONE);

                if (areaTotal!=null){
                    binding.tvNone1.setText("Total Area: ");
                    binding.tvValue1.setText(areaTotal);
                }
                if (value!=null){
                    binding.tvNone2.setText("Total Price: ");
                    binding.tvValue2.setText(value);
                }

                break;
        }
        binding.returnBtn.setOnClickListener(view -> onBackPressed());
    }
}