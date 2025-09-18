package com.ba.housedrawba.adspackage;
import static com.ba.housedrawba.adspackage.AppController.IN_APP_PURCHASE_KEY;
import static com.ba.housedrawba.adspackage.AppController.isShowAdOnInAppClose;
import static com.ba.housedrawba.adspackage.AppController.showInterstitialAdWithLoader;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.R;
import com.ba.housedrawba.Utils.SharedPreferenceManager;
import com.ba.housedrawba.databinding.LayoutPurchaseDialogBinding;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
public class MyBillingManager {
    Activity activity;
    public SharedPreferenceManager sharedPreferenceManager;
    /*..........Billing..........*/
    String TAG = "BillingInit_Main_";
    public BillingClient billingClient;
    public Purchase purchase;
    public ProductDetails singleProduct;
    public List<ProductDetails> myProdDetailsList;
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;

    public void billingSetup(Activity activity) {
        this.activity = activity;
        sharedPreferenceManager = new SharedPreferenceManager(activity, IN_APP_PURCHASE_KEY);
        initPremiumDialog(activity);

        PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (Purchase pur : purchases) {
                    purchase = pur;
//                    verifySubPurchase(pur);
                    if (pur.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
                        showToast("Purchased Succeed!",activity);
                    if (premiumDialog != null) premiumDialog.dismiss();
                }
                handlePurchase(purchase);
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                Log.e(TAG, "onPurchasesUpdated: Purchase Canceled");

            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                Log.e(TAG, "onPurchasesUpdated: Already Purchased");
                sharedPreferenceManager.setBooleanValue(IN_APP_PURCHASE_KEY, true);
            } else {
                Log.e(TAG, "onPurchasesUpdated: Error");
            }
        };
        billingClient = BillingClient.newBuilder(activity)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        //start the connection after initializing the billing client
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                    Log.e(TAG, "OnBillingSetupFinish connected = Already Owned");
                }
                else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    queryProduct();
//                    consumePurchase();
                    Log.e(TAG, "OnBillingSetupFinish connected = ");
                }
                else {
                    Log.e(TAG, "OnBillingSetupFinish failed");
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                Log.i(TAG, "OnBillingSetupFinish connection lost");
            }
        });
    }

    public void queryProduct() {
        myProdDetailsList = new ArrayList<>();
        QueryProductDetailsParams queryProductDetailsParams2 = QueryProductDetailsParams.newBuilder()
                .setProductList(ImmutableList.of(
                        QueryProductDetailsParams.Product.newBuilder().setProductId("premium")
                                .setProductType(BillingClient.ProductType.SUBS).build())).build();

        billingClient.queryProductDetailsAsync(queryProductDetailsParams2, (billingResult, productDetailList) -> {
            if (!productDetailList.isEmpty()) {
                Log.d(TAG+"_22", "billingResult = "+productDetailList.size()+":=:"+ billingResult);
                Log.d(TAG+"_23", productDetailList.get(0).getName() + " = "+
                        productDetailList.get(0).getName() + " = "+
                        productDetailList.get(0).getDescription() + " = "+
                        productDetailList.get(0).getProductType() + " = "+
                        productDetailList.get(0).getTitle() + " = "+
                        productDetailList.get(0).getSubscriptionOfferDetails().get(0).getOfferId() + " 0= "+
                        productDetailList.get(0).getSubscriptionOfferDetails().size()+ " = "+
                        productDetailList.get(0).getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList().get(0).getBillingPeriod() + " = "+
                        productDetailList.get(0).getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice() + " = "+
                        productDetailList.get(0).getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList().get(0).getPriceAmountMicros() + " = "+
                        productDetailList.get(0).getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList().get(0).getPriceCurrencyCode() + " = "+
                        productDetailList.get(0).getSubscriptionOfferDetails().get(0).getBasePlanId()
                );

                myProdDetailsList.add(productDetailList.get(0));
                if (dialogBinding!=null && productDetailList.get(0).getSubscriptionOfferDetails() != null) {
                    String price = productDetailList.get(0).getSubscriptionOfferDetails().get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice();
                    dialogBinding.tvPrice.setText(price+" per week - Cancel anytime");
                }
                Log.d(TAG, "onProductDetailsResponse: Initialized " + productDetailList.size() + " - " + productDetailList.get(0));
            } else {
                Log.e(TAG, "onProductDetailsResponse: No products");
            }
        });
    }
    public void makePurchase(Activity activity) {
//        if (billingClient!=null && activity!=null && productDetails!=null && token!=null) {
        if (billingClient!=null && activity!=null && this.singleProduct!=null) {
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(ImmutableList.of(BillingFlowParams.ProductDetailsParams
                            .newBuilder().setProductDetails(this.singleProduct)
                            .build())).build();
            billingClient.launchBillingFlow(activity, billingFlowParams);
        }
    }

    public void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            AppController.isInAppPurchased=true;
            sharedPreferenceManager.setBooleanValue(IN_APP_PURCHASE_KEY,true);
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken()).build();
                acknowledgePurchaseResponseListener = billingResult -> { };
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
                showToast("Succeed! Thanks for Purchasing. Enjoy Ads free App",activity);
            }
        } else {
            AppController.isInAppPurchased=false;
            sharedPreferenceManager.setBooleanValue(IN_APP_PURCHASE_KEY,false);
        }
    }
    public void consumePurchase() {
        if (purchase != null){
            ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
            ConsumeResponseListener listener = (billingResult, purchaseToken) -> {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    showToast("Purchase Consumed",activity);
                }
            };
            if (billingClient != null) billingClient.consumeAsync(consumeParams, listener);
        }
    }

    void launchPurchaseFlow(Activity activity,ProductDetails productDetails, int itemIndex) {
        if (productDetails.getSubscriptionOfferDetails() != null) {
            if (itemIndex>-1) {
                ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(productDetails)
                                .setOfferToken(productDetails.getSubscriptionOfferDetails().get(itemIndex).getOfferToken())
                                .build()
                );
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList).build();
                billingClient.launchBillingFlow(activity, billingFlowParams);
            }
            else{
                showToast("Some Error Occurred. Please try again later",activity);
            }
        }else{
            showToast("Some Error Occurred. Please try again later",activity);
        }


    }

    void verifySubPurchase(Purchase purchases) {
        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchases.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                //user prefs to set premium
                Log.d(TAG+"_22", "Subscription activated, Enjoy!");
            }
        });

        Log.d(TAG+"_22", "Purchase Token: " + purchases.getPurchaseToken());
        Log.d(TAG+"_22", "Purchase Time: " + purchases.getPurchaseTime());
        Log.d(TAG+"_22", "Purchase OrderID: " + purchases.getOrderId());
    }
    /*............................................*/


    LayoutPurchaseDialogBinding dialogBinding;
    public Dialog premiumDialog;
    public void initPremiumDialog(Activity activity) {
        dialogBinding = LayoutPurchaseDialogBinding.inflate(activity.getLayoutInflater());
        premiumDialog = new Dialog(activity);
        premiumDialog.setContentView(dialogBinding.getRoot());
        premiumDialog.setCancelable(true);
//        premiumDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (premiumDialog.getWindow()!=null) {
            premiumDialog.getWindow().setStatusBarColor(Color.TRANSPARENT);
            premiumDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            premiumDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialogBinding.tvPolicy.setOnClickListener(view -> {
            Intent intent2 = new Intent(Intent.ACTION_VIEW);
            intent2.setData(Uri.parse(activity.getResources().getString(R.string.account_privacy_policy_url)));
            activity.startActivity(intent2);
        });
        dialogBinding.closeBtn.setOnClickListener(view -> premiumDialog.dismiss());
        dialogBinding.continueBtn.setOnClickListener(view -> {
            if (myProdDetailsList!=null && !myProdDetailsList.isEmpty()) {
                ((BaseActivity) activity).triggerFirebaseInAppEvent(activity,"premDialogBtnClick_Purchase");
                launchPurchaseFlow(activity, myProdDetailsList.get(0), 0);
            }
            else{
                showToast("Some Error in Billing SDK! Please try again later",activity);
            }
//            isAllowedAppOpen = false;
//            showPurchaseDialog(activity,billingList);
        });

        premiumDialog.setOnDismissListener(dialogInterface -> {
            ((BaseActivity) activity).triggerFirebaseInAppEvent(activity,"premDialogBtnClick_Close");
            if (isShowAdOnInAppClose) showInterstitialAdWithLoader(activity,null);
        });

    }
    public void showPremiumDialog(){
        if (premiumDialog!=null) premiumDialog.show();
    }

    /*...........................................................*/

    public void showToast(String msg, Context context) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        new Handler().postDelayed(() -> {
            if (toast != null)
                toast.cancel();

        }, 1500);
        if (toast != null)
            toast.show();
    }
}
