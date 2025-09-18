package com.ba.housedrawba.activities;
import static com.ba.housedrawba.adspackage.AppController.isInAppPurchased;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.adspackage.SimpleBillingManager;
import com.ba.housedrawba.databinding.ActivityAiGeneratingDesMainBinding;
import com.ba.housedrawba.interfaces.RewardAdDismissedListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
public class AiGeneratingDesMainActivity extends BaseActivity {
    ActivityAiGeneratingDesMainBinding binding;
    Activity activity = this;
    SimpleBillingManager simpleBillingManager;
    String apiKey0 = "pEmQWjd3yQtOGPJ6szltlMeXSHgWcsT7uYZPlV5YpywJpjns9ULYhxFjMHOi";

    public interface UploadCallback {
        void onUploadSuccess(String imageUrl);
        void onError(String errorMessage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAiGeneratingDesMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        triggerInAppEventForCategories(this,AiGeneratingActivityEventString);
//        initNativeAdSingleSmallAiGen(this, binding.nativeAdViewContainer);

        if (!isInAppPurchased) {
//            createAndGetAdaptiveAd(activity, binding.adViewContainer, getBannerAd(2));
            simpleBillingManager = new SimpleBillingManager(this, new SimpleBillingManager.OnBillingCallback() {
                @Override
                public void onPurchaseSuccess() {
                    isFromWatchAd = false;
                    recreate();
                    Log.d("ModelsLab__2", "Purchase dialog dismissed: "+isInAppPurchased
                            + " - "+isRegen+ " - "+isUploaded+" - ");
                }
                @Override
                public void onDialogDismiss() {
                    if (isInAppPurchased){
                        recreate();
                        showToast("Thanks for purchasing Premium Subscription.");
                    }
                    Log.d("ModelsLab__2", "Purchase dialog dismissed: "+isInAppPurchased
                            + " - "+isRegen+ " - "+isUploaded +" - ");
                }
            });
        }
        initViews();
    }

    private String uploadedUri;
    private String outputUri;
    private String inputImgPath;
    private String roomType;
    private String outStyle;
    private String outMode;
    private String outColor;
    private String prompt, ntvPrompt;
    private Bitmap mBitmap;
    private boolean isUploaded = false;
    private boolean isFromWatchAd = false;
    private boolean isUserAlreadyWatchedAd = false;
    private boolean is3dInput = false;
    private boolean isRegen;
    private boolean isTypeSketch;

    private void initViews() {
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        inputImgPath = getIntent().getStringExtra("imgUri");
        roomType = getIntent().getStringExtra("roomType");
        outStyle = getIntent().getStringExtra("outStyle");
        outMode = getIntent().getStringExtra("outMode");
        outColor = getIntent().getStringExtra("outColor");
        isRegen = getIntent().getBooleanExtra("isRegen", false);
        is3dInput = getIntent().getBooleanExtra("is3dInput", false);
        boolean isSampleImg = getIntent().getBooleanExtra("isSampleImg", false);
        isTypeSketch = getIntent().getBooleanExtra("isTypeSketch", false);
        if (isSampleImg) isRegen = true;

        if (is3dInput){

        }else{
            if (!isTypeSketch){
                prompt = "Realistic interior of a " + roomType +
                        " with the existing " + outMode +
                        " and layout intact. Apply a " + outStyle +
                        " decor style. Do not modify sofa, bed, or room structure. Change wall colors to " + outColor +
                        " and enhance lighting for a natural look.";

                ntvPrompt = "different layout, changed furniture, removed objects, distorted structure, abstract, cartoon, low-res, blurred";

//        String myPrompt = FirebaseRemoteConfig.getInstance().getString("apiKey");
                Log.d("ModelsLab_0", "inputImgPath: " + inputImgPath);
                Log.d("ModelsLab_0", "roomType: " + roomType);
                Log.d("ModelsLab_0", "outStyle: " + outStyle);
                Log.d("ModelsLab_0", "outMode: " + outMode);
                Log.d("ModelsLab_0", "outColor: " + outColor);
//        Log.d("ModelsLab_1", "apiKey: " + myPrompt);
            }
        }
        funLetStartGenerating();

        binding.retryBtn.setOnClickListener(v -> {
            binding.retryBtn.setVisibility(View.GONE);
            if (isUploaded){
                if (isInAppPurchased)
                    startImageGeneration(activity, uploadedUri, prompt, ntvPrompt);
                else if (isUserAlreadyWatchedAd) startImageGeneration(activity, uploadedUri, prompt, ntvPrompt);
                else showFreeGeneratingLayout();
            }else {
                funStartUploading(mBitmap);
            }
        });
        binding.viewBtn.setOnClickListener(v -> {
            if (is3dInput){
                if (outputUri!=null) {
                    startActivity(new Intent(activity, Ai3dPreviewActivity.class)
                            .putExtra("isFromGenerating", true)
                            .putExtra("inputImgUri", inputImgPath)
                            .putExtra("uploadedInputImgUri", uploadedUri)
                            .putExtra("outputImgUri", outputUri)
                            .putExtra("roomType",roomType)
                            .putExtra("outStyle",outStyle)
                            .putExtra("outColor",outColor)
                            .putExtra("outMode",outMode));
                    finish();
                }
            }else{
                if (outputUri!=null) {
                    startActivity(new Intent(activity, AiPreviewResultActivity.class)
                            .putExtra("isFromGenerating", true)
                            .putExtra("inputImgUri", inputImgPath)
                            .putExtra("uploadedInputImgUri", uploadedUri)
                            .putExtra("outputImgUri", outputUri)
                            .putExtra("roomType",roomType)
                            .putExtra("outStyle",outStyle)
                            .putExtra("outColor",outColor)
                            .putExtra("outMode",outMode));
                    finish();
                }
            }
        });
    }

    private void funLetStartGenerating(){
        if (inputImgPath!=null) {
            initFreeGenOptions();
            Glide.with(activity.getApplicationContext())
                    .asBitmap()
                    .load(inputImgPath)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            mBitmap = resource;
                            if (isRegen) {
                                isUploaded = true;
                                uploadedUri = inputImgPath;
                                if (!isInAppPurchased) showFreeGeneratingLayout();
                                else startImageGeneration(activity, uploadedUri, prompt, ntvPrompt);
                            }else{
                                funStartUploading(resource);
                            }
                        }
                        @Override public void onLoadCleared(@Nullable Drawable placeholder) {}});
        }
    }

    private void showErrorLayout() {
        binding.mcvIv.setVisibility(View.GONE);
        binding.llError.setVisibility(View.VISIBLE);
        binding.retryBtn.setVisibility(View.VISIBLE);
        binding.llUploading.setVisibility(View.GONE);
        binding.consProcessing.setVisibility(View.GONE);
        binding.constraintStartGenerating.setVisibility(View.GONE);
    }
    public void initFreeGenOptions() {
        binding.nextBtn.setOnClickListener(v -> {
            if (simpleBillingManager!=null) simpleBillingManager.showPremiumDialog();
        });
        binding.watchAdBtn.setOnClickListener(view -> {
            showRewardedAdSingle(activity, new RewardAdDismissedListener() {
                @Override
                public void onAdDismissed(boolean value) {
                    isUserAlreadyWatchedAd = true;
                    Log.d("ModelsLab__1", "rewarded Ad dismissed: "+value);
                }
                @Override
                public void onRewardAdLoaded() {
                    isFromWatchAd = true;
                    Log.d("ModelsLab__1", "rewarded Ad loaded: " );
                    startImageGeneration(activity, uploadedUri, prompt, ntvPrompt);
                }
            });
            Log.d("ModelsLab__1", "watch ad button clicked: ");
        });
    }
    private void showUploadingLayout() {
        binding.mcvIv.setVisibility(View.GONE);
        binding.llError.setVisibility(View.GONE);
        binding.llUploading.setVisibility(View.VISIBLE);
        binding.consProcessing.setVisibility(View.GONE);
        binding.constraintStartGenerating.setVisibility(View.GONE);
    }
    private void showFinalImageLayout() {
        binding.mcvIv.setVisibility(View.VISIBLE);
        binding.llError.setVisibility(View.GONE);
        binding.llUploading.setVisibility(View.GONE);
        binding.consProcessing.setVisibility(View.GONE);
        binding.viewBtn.setVisibility(View.VISIBLE);
        binding.constraintStartGenerating.setVisibility(View.GONE);
    }
    private void showAiProcessingLayout() {
        binding.mcvIv.setVisibility(View.GONE);
        binding.llError.setVisibility(View.GONE);
        binding.llUploading.setVisibility(View.GONE);
        binding.constraintStartGenerating.setVisibility(View.GONE);
        binding.consProcessing.setVisibility(View.VISIBLE);
    }
    private void showFreeGeneratingLayout(){
        binding.mcvIv.setVisibility(View.GONE);
        binding.llError.setVisibility(View.GONE);
        binding.retryBtn.setVisibility(View.GONE);
        binding.llUploading.setVisibility(View.GONE);
        binding.consProcessing.setVisibility(View.GONE);
        binding.constraintStartGenerating.setVisibility(View.VISIBLE);
    }

    private void funStartUploading(Bitmap bitmap) {
        showUploadingLayout();
        if (bitmap == null){
            Glide.with(activity.getApplicationContext())
                    .asBitmap()
                    .load(inputImgPath)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            mBitmap = resource;
                            uploadToS3UsingCognito(activity, mBitmap, "uploads_", new UploadCallback() {
                                @Override
                                public void onUploadSuccess(String imageUrl) {
                                    isUploaded = true;
                                    uploadedUri = imageUrl;
                                    if (isInAppPurchased)
                                        startImageGeneration(activity, uploadedUri, prompt, ntvPrompt);
                                    else showFreeGeneratingLayout();
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    showErrorLayout();
                                    Log.e("ModelsLab_S3Upload", "Error uploading to S3: " + errorMessage);
                                }
                            });
                        }
                        @Override public void onLoadCleared(@Nullable Drawable placeholder) {}});
        }else{
            uploadToS3UsingCognito(activity, bitmap, "uploads_", new UploadCallback() {
                @Override
                public void onUploadSuccess(String imageUrl) {
                    isUploaded = true;
                    uploadedUri = imageUrl;
                    if (isInAppPurchased) startImageGeneration(activity, uploadedUri, prompt, ntvPrompt);
                    else showFreeGeneratingLayout();
//                startSceneChangeProcess(activity, imageUrl, prompt, ntvPrompt);
                }
                @Override
                public void onError(String errorMessage) {
                    showErrorLayout();
                    Log.e("ModelsLab_S3Upload", "Error uploading to S3: " + errorMessage);
//                                    showErrorDialog(activity,"Error uploading to S3: " + errorMessage);
                }
            });
        }
    }

    public void uploadToS3UsingCognito(Context context, Bitmap bitmap, String fileName, UploadCallback callback) {
        // Save bitmap to temp file
        File file = new File(context.getCacheDir(), fileName);
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            callback.onError("Failed to save bitmap.");
            return;
        }
        String region = "us-west-2";
        String bucketName = "gvai-uploads";
        String identityPoolId = "us-west-2:8b611b09-b8f7-4956-b809-e7b2242ea67c";

        // Create Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context.getApplicationContext(),
                identityPoolId, // Identity Pool ID
                Regions.fromName(region) // Region
        );

        AmazonS3 s3Client = new AmazonS3Client(credentialsProvider);
        s3Client.setRegion(Region.getRegion(Regions.fromName(region)));

        TransferUtility transferUtility = TransferUtility.builder()
                .context(context)
                .s3Client(s3Client)
                .build();

        String objectKey = "house/" + UUID.randomUUID().toString() + ".png";

        TransferObserver uploadObserver = transferUtility.upload(bucketName, objectKey, file);
        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    String s3Url = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + objectKey;
                    callback.onUploadSuccess(s3Url);
                } else if (state == TransferState.FAILED) {
                    callback.onError("Upload failed.");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                Log.d("ModelsLab_S3Upload", "Progress: " + (100 * bytesCurrent / bytesTotal) + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
                Log.e("ModelsLab_S3Upload", "Error: " + ex.getMessage());
                callback.onError("Upload error: " + ex.getMessage());
            }
        });
    }

    public String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }
    private void runLoadingTask(int etaSeconds) {
        new CountDownTimer(etaSeconds * 1000L, 1000) { // millisInFuture, countDownInterval
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                binding.tvSubLoading.setText("It seems your internet is slow. Taking time as usual to fetch the results. Please don't quite the screen otherwise you'll loss your results.");
                // Optionally update a TextView
//                myTextView.setText("Time left: " + secondsRemaining + " sec");
            }
            public void onFinish() {
                Log.d("Timer", "Timer finished!");
            }

        }.start();
    }

    private void fetchGeneratedImageWithRetry(Context context, String apiKey, String fetchId, int delaySeconds) {
        String url;
        if (is3dInput) url = "https://modelslab.com/api/v6/3d/fetch";
        else url = "https://modelslab.com/api/v6/images/fetch";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("key", apiKey);
            requestBody.put("request_id", fetchId);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ModelsLab_RequestError", "Failed to prepare fetch request: " + e.getMessage());
            return;
        }
        Log.d("ModelsLab_FetchRequest", "Fetching image with ID: " + fetchId);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    Log.d("ModelsLab_FetchResponse", "Fetch response: " + response.toString());

                    try {
                        String status = response.getString("status");

                        if (status.equalsIgnoreCase("processing")) {
                            Log.d("ModelsLab_FetchRetry", "Image still processing, retrying in " + delaySeconds + " seconds...");
                            new Handler(Looper.getMainLooper())
                                    .postDelayed(() -> {
                                        fetchGeneratedImageWithRetry(context, apiKey, fetchId, delaySeconds);
                                        runLoadingTask(delaySeconds);
                                    },delaySeconds * 1000L);

                        } else {
                            JSONArray proxyLinks = response.getJSONArray("proxy_links");
                            if (proxyLinks.length() > 0) {
                                String imageDownloadUrl = proxyLinks.getString(0);
                                Log.d("ModelsLab_FetchSuccess", "Image URL: " + imageDownloadUrl);
                                loadImage(imageDownloadUrl);
                            } else {
                                showErrorLayout();
                                Log.e("ModelsLab_FetchError", "No image URL found in fetch response.");
                            }
                        }
                    } catch (JSONException e) {
                        showErrorLayout();
                        e.printStackTrace();
                        Log.e("ModelsLab_FetchError", "Failed to parse fetch response: " + e.getMessage());
                    }
                },
                error -> {
                    String errorMsg = "Fetch Error: " + error.toString();
                    Log.e("ModelsLab_VolleyFetchError", errorMsg);
                    showErrorLayout();
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(jsonObjectRequest);
    }

    public void startImageGeneration(Context context, String initImageUrl, String prompt, String negativePrompt) {
        if (isTypeSketch) {
            String posPrompt = "rendering floor plan of the apartment layout,top view,white background,masterpiece, kitchen, living room, sofa, chair,bedroom,car best quality, extremely detailed,best illustration, best shadow, draw only black borders on white background";
            startSketchToImageGenerating(context, initImageUrl, posPrompt);
        }
        else if (is3dInput){
            start3dModelGenerating(context, initImageUrl);
        }
        else{
            String myPrompt = FirebaseRemoteConfig.getInstance().getString("apiKey");
            String url = "https://modelslab.com/api/v6/interior/make";
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JSONObject requestBody = new JSONObject();
            try {
                showAiProcessingLayout();
                requestBody.put("init_image", initImageUrl);
                requestBody.put("prompt", prompt);
                requestBody.put("negative_prompt", negativePrompt);
                requestBody.put("strength", 0.6);
                requestBody.put("guidance_scale", 3.5);
            } catch (JSONException e) {
                e.printStackTrace();
                showErrorLayout();
                Log.e("ModelsLab_RequestError", "Failed to prepare request: " + e.getMessage());
                return;
            }
            Log.d("ModelsLab_RequestStart", "Sending image generation request: " + requestBody.toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                    response -> {
                        Log.d("ModelsLab_Response", "Image generation response: " + response.toString());
                        try {
                            String status = response.getString("status");
                            switch (status) {
                                case "success":
                                    JSONArray proxyLinks = response.getJSONArray("output");
                                    if (proxyLinks.length() > 0) {
                                        String imageDownloadUrl = proxyLinks.getString(0);
                                        loadImage(imageDownloadUrl);
                                        Log.d("ModelsLab_Response", "Image URL: " + proxyLinks);
                                    }
                                    break;
                                case "processing":
                                    int eta = response.getInt("eta");
                                    int fetchId = response.getInt("id");
                                    Log.d("ModelsLab_Response", "ETA: " + eta + " seconds, Fetch ID: " + fetchId);
                                    fetchGeneratedImageWithRetry(context, myPrompt, String.valueOf(fetchId), eta);
                                    break;
                                case "error":
                                    showErrorLayout();
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showErrorLayout();
                            Log.e("ModelsLab_ResponseError", "Failed to parse response: " + e.getMessage());
                        }
                    },
                    error -> {
                        String errorMsg = "Error: " + error.toString();
                        Log.e("ModelsLab_VolleyRequestError", "Request Failed. try again" + errorMsg);
                        showErrorLayout();
                    }) {

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("key", myPrompt);
                    return headers;
                }
            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            requestQueue.add(jsonObjectRequest);
        }
    }
    public void startSketchToImageGenerating(Context context, String initImageUrl, String prompt) {
//        String myPrompt = FirebaseRemoteConfig.getInstance().getString("apiKey");
        String myPrompt = apiKey0;
        String url = "https://modelslab.com/api/v6/interior/floor_planning";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject requestBody = new JSONObject();
        try {
            showAiProcessingLayout();
            requestBody.put("init_image", initImageUrl);
            requestBody.put("strength", 0.6);
            requestBody.put("prompt", prompt);
            requestBody.put("scenario", "plain");
            requestBody.put("guidance_scale",7);
        } catch (JSONException e) {
            e.printStackTrace();
            showErrorLayout();
            Log.e("ModelsLab_RequestError", "Failed to prepare request: " + e.getMessage());
            return;
        }
        Log.d("ModelsLab_RequestStart", "Sending image generation request: " + requestBody.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    Log.d("ModelsLab_Response", "Image generation response: " + response.toString());
                    try {
                        String status = response.getString("status");
                        switch (status) {
                            case "success":
                                JSONArray proxyLinks = response.getJSONArray("output");
                                if (proxyLinks.length() > 0) {
                                    String imageDownloadUrl = proxyLinks.getString(0);
                                    loadImage(imageDownloadUrl);
                                    Log.d("ModelsLab_Response", "Image URL: " + proxyLinks);
                                }
                                break;
                            case "processing":
                                int eta = response.getInt("eta");
                                int fetchId = response.getInt("id");
                                Log.d("ModelsLab_Response", "ETA: " + eta + " seconds, Fetch ID: " + fetchId);
                                fetchGeneratedImageWithRetry(context, myPrompt, String.valueOf(fetchId), eta);
                                break;
                            case "error":
                                showErrorLayout();
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showErrorLayout();
                        Log.e("ModelsLab_ResponseError", "Failed to parse response: " + e.getMessage());
                    }
                },
                error -> {
                    String errorMsg = "Error: " + error.toString();
                    Log.e("ModelsLab_VolleyRequestError", "Request Failed. try again"+errorMsg);
                    showErrorLayout();
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("key", myPrompt);
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(jsonObjectRequest);
    }
    public void start3dModelGenerating(Context context, String initImageUrl) {
//        String myPrompt = FirebaseRemoteConfig.getInstance().getString("apiKey");
        String myPrompt = apiKey0;
        String url = "https://modelslab.com/api/v6/3d/image_to_3d";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject requestBody = new JSONObject();
        try {
            showAiProcessingLayout();
            requestBody.put("image", initImageUrl);
            requestBody.put("output_format", "glb");
        } catch (JSONException e) {
            e.printStackTrace();
            showErrorLayout();
            Log.e("ModelsLab_RequestError_3D", "Failed to prepare request: " + e.getMessage());
            return;
        }
        Log.d("ModelsLab_RequestStart_3D", "Sending image generation request: " + requestBody.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    Log.d("ModelsLab_Response_3D", "Image generation response: " + response.toString());
                    try {
                        String status = response.getString("status");
                        switch (status) {
                            case "success":
                                JSONArray proxyLinks = response.getJSONArray("output");
                                if (proxyLinks.length() > 0) {
                                    String imageDownloadUrl = proxyLinks.getString(0);
                                    loadImage(imageDownloadUrl);
                                    Log.d("ModelsLab_Response_3D", "Image URL: " + proxyLinks);
                                }
                                break;
                            case "processing":
                                int eta = response.getInt("eta");
                                int fetchId = response.getInt("id");
                                Log.d("ModelsLab_Response_3D", "ETA: " + eta + " seconds, Fetch ID: " + fetchId);
                                fetchGeneratedImageWithRetry(context, myPrompt, String.valueOf(fetchId), eta);
                                break;
                            case "error":
                                showErrorLayout();
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showErrorLayout();
                        Log.e("ModelsLab_ResponseError_3D", "Failed to parse response: " + e.getMessage());
                    }
                },
                error -> {
                    String errorMsg = "Error: " + error.toString();
                    Log.e("ModelsLab_VolleyRequestError_3D", "Request Failed. try again"+errorMsg);
                    showErrorLayout();
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("key", myPrompt);
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }

    private void loadImage(String imageUri) {
        outputUri = imageUri;
        if (is3dInput){
            Log.d("ModelsLab_Response_3D", ": 3d Model generated - "+imageUri);
            showToast("Your 3d model is generated please click the preview button to open it.");
            showFinalImageLayout();

        }else{
            Glide.with(binding.imageView)
                    .asBitmap()
                    .load(imageUri)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            showFinalImageLayout();
                            binding.imageView.setImageBitmap(resource);
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) { }
                    });
        }

    }
}