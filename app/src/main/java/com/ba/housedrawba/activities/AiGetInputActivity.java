package com.ba.housedrawba.activities;

import static androidx.core.content.FileProvider.getUriForFile;
import static com.ba.housedrawba.adspackage.AppController.adClickCounter;
import static com.ba.housedrawba.adspackage.AppController.adsShowInterval;
import static com.ba.housedrawba.adspackage.AppController.createAndGetAdaptiveAd;
import static com.ba.housedrawba.adspackage.AppController.showInterstitialAdWithLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.R;
import com.ba.housedrawba.databinding.ActivityAiGetInputBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yalantis.ucrop.UCrop;

import java.io.File;
public class AiGetInputActivity extends BaseActivity {
    ActivityAiGetInputBinding binding;
    Activity activity = this;
    Context context = this;
    private static final String TAG = "SketchToAiActivityTag_";

    String inputImgUri;
    boolean isAllowedToGoNext;
    Bitmap inputBitmap;
    String style = "black and white";
    String outMode = "keep structure as it is", outColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAiGetInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        triggerInAppEventForCategories(this,AiSketchToAiActivityString);
        createAndGetAdaptiveAd(activity,binding.adViewContainer);
        if (adClickCounter>0 && adClickCounter%adsShowInterval==0) showInterstitialAdWithLoader(activity,null);

        initViews();
    }

    private void initViews(){
        boolean isFromSaved = getIntent().getBooleanExtra("isFromSaved",false);
        String imgUri = getIntent().getStringExtra("imgUri");

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        initActivityResultLaunchers();
        binding.closeImg.setOnClickListener(view -> clearInputImg());

//        binding.llTop.setOnClickListener(view -> showImgPickerOptionDialog(activity));
        binding.galleryBtn.setOnClickListener(view -> {
            if (!((BaseActivity) activity).checkCameraAppPermission(activity)) {
                startActivity(new Intent(activity, TakePermissionsActivity.class).putExtra("image", ""));
            } else {
                Intent galleryIntent = ((BaseActivity) activity).getGalleryIntent();
                galleryLauncher.launch(galleryIntent);
            }
        });
        binding.cameraBtn.setOnClickListener(view -> {
            if (!((BaseActivity) activity).checkCameraAppPermission(activity)) {
                startActivity(new Intent(activity, TakePermissionsActivity.class).putExtra("image", ""));
            } else {
                fileName = System.currentTimeMillis() + ".png";
                Intent cameraIntent = ((BaseActivity) activity).getCameraIntent(activity, fileName);
                cameraLauncher.launch(cameraIntent);
            }
        });

//        binding.mcvRoomColor.setOnClickListener(v -> showInputDialog3Dialog(activity,outColor));
//        binding.mcvRoomMode.setOnClickListener(v -> showInputDialog4Dialog(activity,outMode));


        binding.nextBtn.setOnClickListener(view -> {
            if (isAllowedToGoNext && inputImgUri!=null){
                Log.d("ModelsLab_9", "inputImgPath: " + inputImgUri);
                Intent intent = new Intent(activity, AiGeneratingDesMainActivity.class);
                intent.putExtra("isTypeSketch",true);
                intent.putExtra("imgUri",inputImgUri);
                intent.putExtra("outStyle",style);
                intent.putExtra("outColor",outColor);
                intent.putExtra("outMode",outMode);
                startActivity(intent);
                finish();
            }else{
//                showToast(getResources().getString(R.string.please_));
                showToast("Select Image to continue");
            }
        });

        Log.d("AiResult__1", "isFromSaved: "+isFromSaved+ " - "+imgUri);
        if (isFromSaved && imgUri!=null) {
            inputImgUri = imgUri;
            loadImage(inputImgUri);
            isAllowedToGoNext = true;
        }
    }

    /*public void showRoomTypeDialog(Activity activity, String value) {
        DialogAiRoomTypeBinding bindingDialog = DialogAiRoomTypeBinding.inflate(activity.getLayoutInflater());
        Dialog dialog1 = new Dialog(activity);
        dialog1.setContentView(bindingDialog.getRoot());
        dialog1.setCancelable(true);
        if (dialog1.getWindow()!=null) {
            dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog1.getWindow().setGravity(Gravity.BOTTOM);
        }
        bindingDialog.closeBtn.setOnClickListener(v -> dialog1.dismiss());
//        bindingDialog.gotItBtn.setOnClickListener(v -> guideDialog.dismiss());
        dialog1.show();

        ArrayList<String> dataList = new ArrayList<>();
        dataList.add("Bedroom");
        dataList.add("Living Room");
        dataList.add("Dining Room");
        dataList.add("Gaming Room");
        dataList.add("Study Room");
        dataList.add("Kitchen");
        dataList.add("Bathroom");
        dataList.add("Home Office");
        dataList.add("Office");
        dataList.add("Other");

        AdapterSelectOpt adapterSelectOpt = new AdapterSelectOpt(dataList, po -> {
            if (bindingDialog.recyclerView.getAdapter() != null)
                ((AdapterSelectOpt) bindingDialog.recyclerView.getAdapter()).setSelected(po,true);

            if (dataList.get(po)!=null){
                roomType = dataList.get(po);
                binding.tvRoomType.setText(roomType);
                dialog1.dismiss();
            }
//            if (data!=null && selectedPosition!=null) selectedPosition.itemDataFragment2(po,data);
        });
        bindingDialog.recyclerView.setAdapter(adapterSelectOpt);

        if (dataList.contains(value)) {
            int index = dataList.indexOf(value);
            if (bindingDialog.recyclerView.getAdapter() != null)
                ((AdapterSelectOpt) bindingDialog.recyclerView.getAdapter()).setSelected(index,true);

            Log.d("AiResult__1", "1index: " + index);
        }
        Log.d("AiResult__1", "0index: " + value);
//        dialog1.setOnDismissListener(dialog -> {});
    }
    public void showInputDialog3Dialog(Activity activity, String value) {
        DialogAiInput3Binding bindingDialog = DialogAiInput3Binding.inflate(activity.getLayoutInflater());
        Dialog dialog1 = new Dialog(activity);
        dialog1.setContentView(bindingDialog.getRoot());
        dialog1.setCancelable(true);
        if (dialog1.getWindow()!=null) {
            dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog1.getWindow().setGravity(Gravity.BOTTOM);
        }
        bindingDialog.ivClose.setOnClickListener(v -> dialog1.dismiss());
//        bindingDialog.gotItBtn.setOnClickListener(v -> guideDialog.dismiss());
        dialog1.show();

        List<AiColorPlateModel> list = ((MainActivity) activity).getAiColorsList(activity);
        List<Object> dataList = new ArrayList<>(list);
        AdapterSelectOpt3 adapter = new AdapterSelectOpt3(dataList,2, po -> {
            if (bindingDialog.recyclerView.getAdapter() != null)
                ((AdapterSelectOpt3) bindingDialog.recyclerView.getAdapter()).setSelected(po, true);
            AiColorPlateModel data = (AiColorPlateModel) dataList.get(po);
            if (data!=null){
                outColor = data.getColors();
                binding.tvRoomColor.setText(outColor);
                dialog1.dismiss();
            }
//            selectedPosition.itemDataFragment4(po,data);
        },null);
        bindingDialog.recyclerView.setAdapter(adapter);


        int index = getMatchedIndex2(list, value);
        if (bindingDialog.recyclerView.getAdapter() != null)
            ((AdapterSelectOpt3) bindingDialog.recyclerView.getAdapter()).setSelected(index,true);
        Log.d("AiResult__3", "1index: " + index + " - "+ value);
//        dialog1.setOnDismissListener(dialog -> {});
    }*/
    private void clearInputImg() {
        isAllowedToGoNext = false;
        binding.nextBtn.setAlpha(0.3f);
        binding.imageView.setImageResource(0);
        binding.imageView.setVisibility(View.GONE);
        binding.closeImg.setVisibility(View.GONE);
        binding.llTop.setVisibility(View.VISIBLE);
        binding.galleryBtn.setVisibility(View.VISIBLE);
        binding.cameraBtn.setVisibility(View.VISIBLE);
    }

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private String fileName;
    private void initActivityResultLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri imageUri = getCacheImagePathForCamera(activity, fileName);
                        if (imageUri != null) {
//                            ((AiGetInputActivity) activity).imgUri = imageUri.toString();
                            cropImage(activity,imageUri);
//                            loadImage(imageUri);
                        }
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
//                            ((AiGetInputActivity) activity).imgUri = imageUri.toString();
                            cropImage(activity,imageUri);
//                            loadImage(imageUri);
                        }
                    }
                });
    }
    private Uri getCacheImagePathForCamera(Activity activity, String fileName) {
        File path = new File(activity.getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        return getUriForFile(activity, activity.getPackageName() + ".provider", image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK && data!=null) {
                Uri resultUri = UCrop.getOutput(data);
                if (resultUri!=null) {
                    inputImgUri = resultUri.toString();
                    isAllowedToGoNext = true;
                    loadImage(inputImgUri);
                }
            } else {
                showToast("No image selected");
            }
        }
    }
    boolean lockAspectRatio = true, setBitmapMaxWidthHeight = true;
    //    int ASPECT_RATIO_X = 16, ASPECT_RATIO_Y = 9, bitmapMaxWidth = 1000, bitmapMaxHeight = 1280;
    int ASPECT_RATIO_X = 1, ASPECT_RATIO_Y = 1, bitmapMaxWidth = 1000, bitmapMaxHeight = 1280;
    int IMAGE_COMPRESSION = 80;
    public void cropImage(Activity activity, Uri sourceUri) {
        String qName = queryName(activity.getContentResolver(), sourceUri);
        Uri destinationUri = null;
        if (qName!=null) destinationUri = Uri.fromFile(new File(activity.getCacheDir(), qName));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(IMAGE_COMPRESSION);
        // applying UI theme
        options.setToolbarColor(ContextCompat.getColor(activity, R.color.white));
        options.setStatusBarColor(ContextCompat.getColor(activity, R.color.primaryColor));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(activity, R.color.primaryColor));
        options.setCropGridStrokeWidth(10);
        options.setFreeStyleCropEnabled(true);
//        options.setHideBottomControls();
//        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.white));
//        options.setAspectRatioOptions();
        if (lockAspectRatio) options.withAspectRatio(ASPECT_RATIO_X, ASPECT_RATIO_Y);
        if (setBitmapMaxWidthHeight) options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight);
        if (destinationUri!=null) UCrop.of(sourceUri, destinationUri).withOptions(options).start(activity);
    }

    private void loadImage(String imageUri) {
        Glide.with(binding.imageView)
                .asBitmap()
                .load(imageUri)
                .encodeQuality(80)
//                .override(768)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        inputBitmap = resource;
                        binding.imageView.setImageBitmap(resource);
                        binding.imageView.setVisibility(View.VISIBLE);
                        binding.closeImg.setVisibility(View.VISIBLE);
                        binding.llTop.setVisibility(View.GONE);
                        binding.galleryBtn.setVisibility(View.GONE);
                        binding.cameraBtn.setVisibility(View.GONE);
                        binding.nextBtn.setAlpha(1);
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) { }
                });
    }
}