package com.ba.housedrawba.activities;
import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.R;
import com.ba.housedrawba.adspackage.AppOpenManager;
import com.ba.housedrawba.databinding.ActivityTakePermissionsBinding;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
public class TakePermissionsActivity extends BaseActivity {
    ActivityTakePermissionsBinding binding;
    Activity activity = this;
    int allowedPermissions = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTakePermissionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        createAndGetAdaptiveAd(activity,binding.adViewContainer,getBannerAd(2));
//        AppController.loadBigBannerAd(binding.adViewContainer);
        initViews();
    }

    private void initViews(){
        UltimateBarX.statusBarOnly(this).fitWindow(true).transparent()
                .colorRes(android.R.color.transparent).light(true).apply();
        binding.backBtn.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        binding.switch1.setOnColor(getResources().getColor(R.color.primaryColor, getTheme()));
        binding.switch2.setOnColor(getResources().getColor(R.color.primaryColor, getTheme()));
        binding.switch3.setOnColor(getResources().getColor(R.color.primaryColor, getTheme()));

        if (Build.VERSION.SDK_INT > 32) {
            binding.switch1Notification.setVisibility(View.VISIBLE);
            binding.viewLine.setVisibility(View.VISIBLE);
//            boolean isAllowed = !shouldShowRequestPermissionRationale("112");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager. PERMISSION_GRANTED){
                allowedPermissions++;
                binding.switch1.setChecked(true,true);
            }else {
                binding.switch1.setChecked(false,true);
            }
        } else{
//            binding.switch1.setChecked(checkAppPermission(activity),true);
            binding.switch1Notification.setVisibility(View.GONE);
            binding.viewLine.setVisibility(View.GONE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.switch2Photos.setVisibility(View.GONE);
        }else{
            binding.switch2Photos.setVisibility(View.VISIBLE);
        }

        if (checkCameraAppPermission(activity)){
            binding.switch3.setChecked(true, true);
            allowedPermissions++;
        }else{
            binding.switch3.setChecked(false, true);
        }
        if (!checkAppPermission(activity)) {
            binding.switch2.setChecked(false, true);
        }else{
            allowedPermissions++;
            binding.switch2.setChecked(true, true);
        }
        funAllPermitted();

        binding.switch1Notification.setOnClickListener(view -> {
            if (!shouldShowRequestPermissionRationale("112") && ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager. PERMISSION_GRANTED) {
                getNotificationPermission();
            }else{
                showToast("Permission Already Granted");
            }
        });
        binding.switch2Photos.setOnClickListener(view -> {
            AppOpenManager.isAllowedOpenAppAd = false;
            if (!checkAppPermission(activity)) {
                requestStoragePermission(activity, () -> {
                    allowedPermissions++;
                    binding.switch2.setChecked(true, true);
                    funAllPermitted();
                });
            }else{
                showToast("Permission Already Granted");
            }
        });
        binding.switch3Camera.setOnClickListener(view -> {
            AppOpenManager.isAllowedOpenAppAd = false;
            if (!checkCameraAppPermission(activity)) {
                requestCameraPermission(activity, () -> {
                    allowedPermissions++;
                    binding.switch3.setChecked(true, true);
                    funAllPermitted();
                });
            }else{
                showToast("Permission Already Granted");
            }
        });
//        binding.switch1.setEnabled(false);
//        binding.switch2.setEnabled(false);
//        binding.switch3.setEnabled(false);

        int data = getIntent().getIntExtra("image",R.drawable.floor_1);
        binding.continueBtn.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("result", data);
            setResult(RESULT_OK, intent);
            finish();
//                startActivity(new Intent(activity, HomeActivity.class));
        });

        binding.switch1.setOnClickListener(v -> {
            if (!shouldShowRequestPermissionRationale("112") && ActivityCompat.checkSelfPermission
                    (activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager. PERMISSION_GRANTED) {
                getNotificationPermission();
            }else{
                showToast("Permission Already Granted");
            }
        });
        binding.switch2.setOnClickListener(v -> {
            AppOpenManager.isAllowedOpenAppAd = false;
            if (!checkAppPermission(activity)) {
                requestStoragePermission(activity, () -> {
                    allowedPermissions++;
                    binding.switch2.setChecked(true, true);
                    funAllPermitted();
                });
            }else{
                showToast("Permission Already Granted");
            }
        });
        binding.switch3.setOnClickListener(v -> {
            AppOpenManager.isAllowedOpenAppAd = false;
            if (!checkCameraAppPermission(activity)) {
                requestCameraPermission(activity, () -> {
                    allowedPermissions++;
                    binding.switch3.setChecked(true, true);
                    funAllPermitted();
                });
            }else{
                showToast("Permission Already Granted");
            }
        });
    }

    private void funAllPermitted(){
        if (allowedPermissions==2){
            binding.constraintMain.setVisibility(View.GONE);
            binding.constraintDone.setVisibility(View.VISIBLE);
        }else{
            binding.constraintMain.setVisibility(View.VISIBLE);
            binding.constraintDone.setVisibility(View.GONE);
        }
    }

    /*<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<*/
    public static final int PERMISSION_REQUEST_CODE = 112;
    public void getNotificationPermission() {
        if (Build.VERSION.SDK_INT > 32) {
            AppOpenManager.isAllowedOpenAppAd = false;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // allow
                allowedPermissions++;
                binding.switch1.setChecked(true,true);
                funAllPermitted();
            } else {
                binding.switch1.setChecked(false,true);
                showDialogOK((dialogInterface, i) -> permissionSettingScreen());
            }
        }
    }
    private void permissionSettingScreen() {
        showToast("Please unblock notifications to receive important Notifications.");
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
        finish();
    }

    private void showDialogOK(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("Please allow to show important notifications.")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        funAllPermitted();
    }
}