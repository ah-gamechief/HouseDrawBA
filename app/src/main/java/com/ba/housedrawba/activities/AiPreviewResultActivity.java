package com.ba.housedrawba.activities;
import static com.ba.housedrawba.adspackage.AppController.createAndGetAdaptiveAd;
import static com.ba.housedrawba.adspackage.AppController.isInAppPurchased;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.R;
import com.ba.housedrawba.Utils.CacheUtils;
import com.ba.housedrawba.Utils.CustomViews.Rubber.RubberStamp;
import com.ba.housedrawba.Utils.CustomViews.Rubber.RubberStampConfig;
import com.ba.housedrawba.Utils.CustomViews.Rubber.RubberStampPosition;
import com.ba.housedrawba.adspackage.SimpleBillingManager;
import com.ba.housedrawba.databinding.ActivityAiPreviewResultBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
public class AiPreviewResultActivity extends BaseActivity {
    ActivityAiPreviewResultBinding binding;
    Activity activity = this;
    SimpleBillingManager simpleBillingManager;

    RubberStampConfig config;
    RubberStamp rubberStamp;
    Bitmap resultBitmap, resultBitmapWithWatermark, inputImgBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAiPreviewResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        triggerInAppEventForCategories(this,AiResultPreviewActivityEventString);

        simpleBillingManager = new SimpleBillingManager(this, new SimpleBillingManager.OnBillingCallback() {
            @Override
            public void onPurchaseSuccess() {

            }

            @Override
            public void onDialogDismiss() {

            }
        });

        createAndGetAdaptiveAd(activity, binding.adViewContainer);

        initViews();
    }

    String inputImgUri, outputImgUri, uploadedInputImgUri;

    private void initViews() {
        binding.btnBack.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
        inputImgUri = getIntent().getStringExtra("inputImgUri");
        outputImgUri = getIntent().getStringExtra("outputImgUri");
        uploadedInputImgUri = getIntent().getStringExtra("uploadedInputImgUri");
        boolean isFromGenerating = getIntent().getBooleanExtra("isFromGenerating", false);


        if (outputImgUri!=null) {
            Glide.with(activity.getApplicationContext())
                    .asBitmap()
                    .load(outputImgUri)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            if (!isInAppPurchased) {
                                config = new RubberStampConfig.RubberStampConfigBuilder()
                                        .base(resource)
                                        .rubberStamp("     Appish Studio     ")
                                        .rubberStampPosition(RubberStampPosition.TILE)
                                        .alpha(140)
                                        .margin(0, 0)
                                        .rotation(45)
                                        .textColor(getResources().getColor(R.color.white))
//                                    .textFont()
//                                    .textBackgroundColor(textBgColor)
                                        .textSize(62)
                                        .build();
                                resultBitmap = resource;
                                rubberStamp = new RubberStamp(activity);
                                Bitmap bitmap = rubberStamp.addStamp(config);
                                binding.imageView.setImageBitmap(bitmap);
                                binding.imageView.setVisibility(View.VISIBLE);
                                binding.loadingView.setVisibility(View.GONE);
                                resultBitmapWithWatermark = bitmap;

//                                if (inputImgUri!=null && isFromGenerating) {
                                if (inputImgUri!=null) {
                                    Glide.with(activity).asBitmap().load(inputImgUri).into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource2, @Nullable Transition<? super Bitmap> transition) {
                                            inputImgBitmap = resource2;
                                            if (isFromGenerating) CacheUtils.saveBitmapPairToCache(activity, bitmap, resource2);
                                        }
                                        @Override public void onLoadCleared(@Nullable Drawable placeholder) {}
                                    });
                                }

                                try {
                                    File cachePath = new File(getCacheDir(), "images");
                                    cachePath.mkdirs(); // Create directory
                                    File file = new File(cachePath, "shared_image.png");
                                    FileOutputStream stream = new FileOutputStream(file);
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    stream.close();
                                    // Get URI using FileProvider
                                    Uri contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                            else {
                                resultBitmap = resource;
                                resultBitmapWithWatermark = resource;
                                binding.imageView.setImageBitmap(resource);
                                binding.imageView.setVisibility(View.VISIBLE);
                                binding.loadingView.setVisibility(View.GONE);

                                if (inputImgUri!=null) {
                                    Glide.with(activity).asBitmap().load(inputImgUri).into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource2, @Nullable Transition<? super Bitmap> transition) {
                                            inputImgBitmap = resource2;
                                            if (isFromGenerating) CacheUtils.saveBitmapPairToCache(activity, resource, resource2);
                                        }
                                        @Override public void onLoadCleared(@Nullable Drawable placeholder) {}
                                    });
                                }

                                try {
                                    File cachePath = new File(getCacheDir(), "images");
                                    cachePath.mkdirs(); // Create directory
                                    File file = new File(cachePath, "shared_image.png");
                                    FileOutputStream stream = new FileOutputStream(file);
                                    resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    stream.close();
                                    // Get URI using FileProvider
                                    Uri contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        @Override public void onLoadCleared(@Nullable Drawable placeholder) {}});
//            binding.beforeView.loadImagesByUrl(inputImgUri, outputImgUri);
            binding.imageView.setOnClickListener(v -> {
                if (!isInAppPurchased) showFullScreenPreView(activity, resultBitmapWithWatermark);
                else  showFullScreenPreView(activity, resultBitmap);
            });
        }

        /*binding.removeWaterMark.setOnClickListener(v -> {
            if (!isInAppPurchased) simpleBillingManager.showPremiumDialog(this);
            else showToast("Watermark Removed");
        });*/

        binding.btnCrossBeforeView.setOnClickListener(v -> {
            binding.imageView.setVisibility(View.VISIBLE);
            binding.btnBack.setVisibility(View.VISIBLE);
            binding.btnBeforeAfter.setVisibility(View.VISIBLE);
            binding.btnCrossBeforeView.setVisibility(View.GONE);
        });
        binding.btnBeforeAfter.setOnClickListener(v -> {
            binding.imageView.setVisibility(View.INVISIBLE);
            binding.btnBack.setVisibility(View.GONE);
            binding.btnBeforeAfter.setVisibility(View.GONE);

            binding.btnCrossBeforeView.setVisibility(View.VISIBLE);
        });


        binding.saveBtn.setOnClickListener(v -> {
            if (!isInAppPurchased) {
                if (resultBitmapWithWatermark!=null) checkAppPermission(activity,Bitmap.CompressFormat.PNG,100,resultBitmapWithWatermark);

            }else {
                if (resultBitmap!=null) checkAppPermission(activity,Bitmap.CompressFormat.PNG,100,resultBitmap);
            }
        });

        /*binding.savePdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.US).format(new Date());
                Bitmap appIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//            Uri pdfUri = PdfUtils.createPdfStacked(this, inputImgBitmap, resultBitmapWithWatermark, appIcon,
//                    "Modern House Designs", "https://play.google.com/store/apps/details?id=" + getPackageName(),
//                    "StackedPdf");
                Uri sideUri = PdfUtils.createPdf(activity, inputImgBitmap, resultBitmapWithWatermark, appIcon,
                        "Modern House Designs", "https://play.google.com/store/apps/details?id=" + getPackageName(),
                        "FloorPlans"+timeStamp, PdfUtils.LayoutStyle.SIDE_BY_SIDE);
// Share
                if (sideUri != null) {
                    showToast("Files saved as Pdf in Documents folder");
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("application/pdf");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, sideUri);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(shareIntent, "Share PDF"));
                }
            }
        });*/

        /*.....................................................*/
        binding.regenBtn.setOnClickListener(v -> {
            Intent intent = new Intent(activity, AiGeneratingDesMainActivity.class);
//            intent.putExtra("isRegen",true);
            intent.putExtra("is3dInput",true);
            intent.putExtra("imgUri",outputImgUri);
//            intent.putExtra("roomType",roomType);
//            intent.putExtra("outStyle",outStyle);
//            intent.putExtra("outColor",outColor);
//            intent.putExtra("outMode",outMode);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
    }

    private void showFullScreenPreView(Context context, Bitmap bitmap){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_layout_img_preview);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        if (window!=null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.black);
        }

        ImageView closeBtn = dialog.findViewById(R.id.close_btn_sq_iv);
        ImageView iv_zoomable = dialog.findViewById(R.id.iv_zoomable);
        closeBtn.setOnClickListener(v1 -> dialog.dismiss());

        if (bitmap!=null) iv_zoomable.setImageBitmap(bitmap);

//        Glide.with(context).asBitmap().load(imgPreviewPath).into(new CustomTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                iv_zoomable.setImageBitmap(resource);
//            }
//            @Override public void onLoadCleared(@Nullable Drawable placeholder) {}});
        dialog.show();
    }
}