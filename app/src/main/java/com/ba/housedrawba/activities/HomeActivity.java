package com.ba.housedrawba.activities;
import static com.ba.housedrawba.adspackage.AppController.createAndGetAdaptiveAd;
import static com.ba.housedrawba.adspackage.AppController.isInAppPurchased;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;

import com.android.billingclient.BuildConfig;
import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.R;
import com.ba.housedrawba.adspackage.AppController;
import com.ba.housedrawba.databinding.ActivityHomeBinding;
import com.ba.housedrawba.databinding.LayoutAppExitDialogBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{
    Activity activity = this;
    ActivityHomeBinding binding;
    Context context  = this;
    /*,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,*/
    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;
    private static final int FLEXIBLE_APP_UPDATE_REQ_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createAndGetAdaptiveAd(activity,binding.mainLayout.adViewContainer);

        billingManager.intPremiumDialog();

        initAppExitDialog(activity);
        initViews();
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        installStateUpdatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                removeInstallStateUpdateListener();
            } else {
                Toast.makeText(getApplicationContext(), "InstallStateUpdatedListener: state: " + state.installStatus(), Toast.LENGTH_LONG).show();
            }
        };
//        appUpdateManager.registerListener(installStateUpdatedListener);
        checkUpdate();
        billingManager.showPremiumDialog();
    }

    private void initViews(){
        UltimateBarX.statusBarOnly(this)
                .fitWindow(true)
                .transparent()
                .colorRes(android.R.color.transparent)
                .light(true)
                .apply();

        binding.mainLayout.homeBtn1.setOnClickListener(this);
        binding.mainLayout.homeBtn2.setOnClickListener(this);

        binding.mainLayout.dshBtnExp1.setOnClickListener(this);
        binding.mainLayout.dshBtnExp2.setOnClickListener(this);
        binding.mainLayout.dshBtnExp3.setOnClickListener(this);
        binding.mainLayout.dshBtnExp4.setOnClickListener(this);
        binding.mainLayout.dshBtnExp5.setOnClickListener(this);
        binding.mainLayout.dshBtnExp6.setOnClickListener(this);
        binding.mainLayout.dshBtnExp7.setOnClickListener(this);
        binding.mainLayout.dshBtnExp8.setOnClickListener(this);
        binding.mainLayout.dshCce1.setOnClickListener(this);
        binding.mainLayout.dshCce2.setOnClickListener(this);
        binding.mainLayout.dshCce3.setOnClickListener(this);
        binding.mainLayout.dshCce4.setOnClickListener(this);
        binding.mainLayout.premiumBtn.setOnClickListener(this);

        binding.navigationView.setNavigationItemSelectedListener(this);
        toggleDrawer();
    }
    /*<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<*/
    private void checkUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            }
        });
    }
    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE,
                    activity, FLEXIBLE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FLEXIBLE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Update canceled by user! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(),"Update success! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Update Failed! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
                checkUpdate();
            }
        }
    }
    private void popupSnackBarForCompleteUpdate() {
        Snackbar.make(findViewById(android.R.id.content).getRootView(), "New app is ready!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Install", view -> {
                    if (appUpdateManager != null) {
                        appUpdateManager.completeUpdate();
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.primaryColor,getTheme()))
                .show();
    }
    private void removeInstallStateUpdateListener() {
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }
    /*<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<*/
    private void toggleDrawer() {
        setSupportActionBar(binding.mainLayout.toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(activity, binding.drawerLayout, binding.mainLayout.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void closeDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
        showExitAppDialog();
    }

    @Override
    public void onClick(View view) {
        AppController.adClickCounter++;
        int id = view.getId();
        if (id == R.id.home_btn1) {
            triggerFirebaseInAppEvent(activity,"AiHomeActivity");
            startActivity(new Intent(activity,AiHomeActivity.class)
                    .putExtra("categoryType","")
            );
        } else if (id == R.id.home_btn2) {
            triggerFirebaseInAppEvent(activity,"CreateNewMapActivity");
            startActivity(new Intent(activity,CreateNewMapActivity.class)
                    .putExtra("categoryType","")
            );

        } else if (id == R.id.dsh_btn_exp_1) {
            triggerFirebaseInAppEvent(activity,"SelectExpDesActivity");
            startActivity(new Intent(context, SelectExpDesActivity.class));
        }
        else if (id == R.id.dsh_btn_exp_2) {
            triggerFirebaseInAppEvent(activity,"ArExploreActivity");
            startActivity(new Intent(context,ArExploreActivity.class));
        }
        else if (id == R.id.dsh_btn_exp_3) {
            triggerFirebaseInAppEvent(activity,"ExploreActivity_Interior");
            startActivity(new Intent(context,ExploreDesignsActivity.class)
                    .putExtra("categoryType",3));
        }
        else if (id == R.id.dsh_btn_exp_4) {
            triggerFirebaseInAppEvent(activity,"ExploreActivity_Exterior");
            startActivity(new Intent(context,ExploreDesignsActivity.class)
                    .putExtra("categoryType",4));
        }else if (id == R.id.dsh_btn_exp_5) {
            triggerFirebaseInAppEvent(activity,"ExploreActivity_PortableHouse");
            startActivity(new Intent(context,ExploreDesignsActivity.class)
                    .putExtra("categoryType",5));
        }else if (id == R.id.dsh_btn_exp_6) {
            triggerFirebaseInAppEvent(activity,"ExploreActivity_RoomDecIdeas");
            startActivity(new Intent(context,ExploreDesignsActivity.class)
                    .putExtra("categoryType",6));
        }else if (id == R.id.dsh_btn_exp_7) {
            triggerFirebaseInAppEvent(activity,"ExploreActivity_BathVanity");
            startActivity(new Intent(context,ExploreDesignsActivity.class)
                    .putExtra("categoryType",7));
        }else if (id == R.id.dsh_btn_exp_8) {
            triggerFirebaseInAppEvent(activity,"ExploreActivity_HomeOffice");
            startActivity(new Intent(context,ExploreDesignsActivity.class)
                    .putExtra("categoryType",8));
        }
        else if (id == R.id.dsh_cce_1) {
            triggerFirebaseInAppEvent(activity,"BricksCalculatorActivity");
            startActivity(new Intent(activity,BricksCalculatorActivity.class)
                    .putExtra("categoryType",""));
        }
        else if (id == R.id.dsh_cce_2) {
            triggerFirebaseInAppEvent(activity,"SteelCalculatorActivity");
            startActivity(new Intent(activity,SteelCalculatorActivity.class)
                    .putExtra("categoryType",""));
        }
        else if (id == R.id.dsh_cce_3) {
            triggerFirebaseInAppEvent(activity,"PaintCalculateActivity");
            startActivity(new Intent(activity,PaintCalculateActivity.class)
                    .putExtra("categoryType",""));
        }
        /*else if (id == R.id.dsh_btn_10) {
            startActivity(new Intent(activity,PlasterCalculationActivity.class)
                    .putExtra("categoryType",""));
        }*/
        else if (id == R.id.dsh_cce_4) {
            triggerFirebaseInAppEvent(activity,"TilesCalculatorActivity");
            startActivity(new Intent(activity,TilesCalculatorActivity.class)
                    .putExtra("categoryType",""));
        }
        else if (id == R.id.premiumBtn) {
            triggerFirebaseInAppEvent(activity,"showPremDialogBtnClicked");
            billingManager.showPremiumDialog();
//            showPurchaseDialog(activity);
//            showPurchaseDialog(activity,billingList);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.mid_0) {
            triggerFirebaseInAppEvent(activity,"MyCreationsActivity");
            startActivity(new Intent(activity,MyCreationsActivity.class)
                    .putExtra("categoryType",""));
        }
        else if (itemId == R.id.mid_1) {
            triggerFirebaseInAppEvent(activity,"rateApp_navItemClick");
            String url = getResources().getString(R.string.app_url);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else if (itemId == R.id.mid_2) {
            triggerFirebaseInAppEvent(activity,"moreApps_navItemClick");
            try {
                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                intent2.setData(Uri.parse(getResources().getString(R.string.app_account_url)));
                startActivity(intent2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (itemId == R.id.mid_3) {
            if (!isInAppPurchased) {
                billingManager.showPremiumDialog();
            } else {
                showToast("You already have purchased");
            }
        } else if (itemId == R.id.mid_4) {
//            consumePurchase();
            Intent intent2 = new Intent(Intent.ACTION_VIEW);
            intent2.setData(Uri.parse(getResources().getString(R.string.account_privacy_policy_url)));
            startActivity(intent2);
        } else if (itemId == R.id.mid_5) {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                String shareMessage = "\n GoGet \n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Choose one"));
            } catch (Exception e) {
                Log.e("Dashboard_01", e.toString());
            }
        }
        else if (itemId == R.id.mid_6) {
            onBackPressed();
        }
        closeDrawer();
        return false;
    }

    Dialog exitAppDialog;
    public void initAppExitDialog(Activity activity){
        LayoutAppExitDialogBinding bindingDialog = LayoutAppExitDialogBinding.inflate(getLayoutInflater());
        exitAppDialog = new Dialog(activity);
        exitAppDialog.setContentView(bindingDialog.getRoot());
        exitAppDialog.setCancelable(true);
        if (exitAppDialog.getWindow()!=null) {
            exitAppDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            exitAppDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            exitAppDialog.getWindow().setGravity(Gravity.BOTTOM);
        }

        if (exitAppDialog!=null) {
            AppController.loadBigBannerAd(activity,bindingDialog.adViewExitDialog);
            bindingDialog.btnExit.setOnClickListener(view -> activity.finishAffinity());
        }

    }
    public void showExitAppDialog(){
        if(exitAppDialog!=null){
            exitAppDialog.show();
        }
    }


}