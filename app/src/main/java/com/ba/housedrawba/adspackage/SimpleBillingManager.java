package com.ba.housedrawba.adspackage;
import static com.ba.housedrawba.adspackage.AppController.IN_APP_PURCHASE_KEY;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.Utils.SharedPreferenceManager;
import com.ba.housedrawba.databinding.LayoutPurchaseDialogBinding;

import java.util.List;
public class SimpleBillingManager {
    private final String SUBSCRIPTION_PRODUCT_ID = "premium";
    private final String TARGET_BASE_PLAN_ID = "weekly2";
    private final String TARGET_OFFER_ID = "offer2";

    private final Activity activity;
    private BillingClient billingClient;
    private ProductDetails currentProductDetails;
    private ProductDetails.SubscriptionOfferDetails targetOfferDetails;
    private ProductDetails.SubscriptionOfferDetails baseOfferDetails;

    private Dialog premiumDialog;
    private LayoutPurchaseDialogBinding dialogBinding;
    private final OnBillingCallback billingCallback;
    private final SharedPreferenceManager sharedPreferenceManager;

    public interface OnBillingCallback {
        void onPurchaseSuccess();
        void onDialogDismiss();
    }

    public SimpleBillingManager(Activity activity, OnBillingCallback callback) {
        sharedPreferenceManager = new SharedPreferenceManager(activity, IN_APP_PURCHASE_KEY);
        this.activity = activity;
        this.billingCallback = callback;
        initBillingClient();
        intPremiumDialog();
    }

    private void initBillingClient() {
        billingClient = BillingClient.newBuilder(activity)
                .enablePendingPurchases()
                .setListener(this::onPurchaseUpdated)
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    queryProductDetails();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Reconnect logic can be added here if needed
            }
        });
    }

    private void queryProductDetails() {
        String tag = "queryProductDetails_01";
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(List.of(
                        QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(SUBSCRIPTION_PRODUCT_ID)
                                .setProductType(BillingClient.ProductType.SUBS)
                                .build()
                )).build();

        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && !productDetailsList.isEmpty()) {
                ProductDetails productDetails = productDetailsList.get(0);
                currentProductDetails = productDetails;
                // Find offer with offerId "offer2-wk"
                if (productDetails.getSubscriptionOfferDetails() != null) {
                    for (ProductDetails.SubscriptionOfferDetails offer : productDetails.getSubscriptionOfferDetails()) {
                        if (TARGET_OFFER_ID.equals(offer.getOfferId())) {
                            targetOfferDetails = offer;
                        } else if (offer.getOfferId() == null && TARGET_BASE_PLAN_ID.equals(offer.getBasePlanId())) {
                            baseOfferDetails = offer;
                        }
                    }
                }
                Log.e(tag,"billing init - "+targetOfferDetails.getOfferId());
                updatePriceInDialog();
            }
        });
    }

    private void onPurchaseUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        }
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
            AcknowledgePurchaseParams acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build();
            billingClient.acknowledgePurchase(acknowledgeParams, result -> {
                if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    sharedPreferenceManager.setBooleanValue(IN_APP_PURCHASE_KEY, true);
                    if (premiumDialog != null) premiumDialog.dismiss();
                    if (billingCallback != null) billingCallback.onPurchaseSuccess();
//                    if (activity!=null) ((MainActivity) activity).triggerInAppEventForCategories(activity,"subscriptionPurchased");
                }
            });
        }
    }

    public void intPremiumDialog() {
        dialogBinding = LayoutPurchaseDialogBinding.inflate(activity.getLayoutInflater());
        premiumDialog = new Dialog(activity);
        premiumDialog.setContentView(dialogBinding.getRoot());
        premiumDialog.setCancelable(true);

        if (premiumDialog.getWindow() != null) {
            premiumDialog.getWindow().setStatusBarColor(Color.TRANSPARENT);
            premiumDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            premiumDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialogBinding.closeBtn.setOnClickListener(v -> {
            premiumDialog.dismiss();
            if (billingCallback != null) billingCallback.onDialogDismiss();
        });


        dialogBinding.continueBtn.setOnClickListener(v -> {
            if (currentProductDetails == null || targetOfferDetails == null) {
                ((BaseActivity) activity).triggerFirebaseInAppEvent(activity,"premDialogBtnClick_Purchase");
                initBillingClient();
                Toast.makeText(activity, "Please wait, Fetching", Toast.LENGTH_SHORT).show();
            } else {
                AppOpenManager.isAllowedOpenAppAd = false;
                launchPurchaseFlow();
            }
        });

        premiumDialog.setOnDismissListener(dialog -> {
            ((BaseActivity) activity).triggerFirebaseInAppEvent(activity,"premDialogBtnClick_Close");
            if (billingCallback != null) billingCallback.onDialogDismiss();
        });
    }

    public void showPremiumDialog() {
        if (activity!=null && premiumDialog!=null) premiumDialog.show();
    }

    private void updatePriceInDialog() {
        if (dialogBinding == null) return;
        activity.runOnUiThread(() -> {
            String originalPriceStr = "";
            // Get discounted price
            if (targetOfferDetails != null) {
                for (ProductDetails.PricingPhase phase : targetOfferDetails.getPricingPhases().getPricingPhaseList()) {
                    if (phase.getPriceAmountMicros() > 0) {
                        break;
                    }
                }
            }
            // Get original base price
            if (baseOfferDetails != null) {
                for (ProductDetails.PricingPhase phase : baseOfferDetails.getPricingPhases().getPricingPhaseList()) {
                    if (phase.getPriceAmountMicros() > 0) {
                        originalPriceStr = phase.getFormattedPrice();
                        break;
                    }
                }
            }
            dialogBinding.tvPrice.setText("After 3 days " +originalPriceStr+ " per week will be charged");
        });
    }
    
    private void launchPurchaseFlow() {
        BillingFlowParams.ProductDetailsParams productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(currentProductDetails)
                .setOfferToken(targetOfferDetails.getOfferToken())
                .build();
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(List.of(productDetailsParams))
                .build();
        if (activity != null) billingClient.launchBillingFlow(activity, billingFlowParams);
    }
}
