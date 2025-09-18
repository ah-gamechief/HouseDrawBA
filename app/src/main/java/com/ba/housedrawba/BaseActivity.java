package com.ba.housedrawba;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.core.content.FileProvider.getUriForFile;
import static com.ba.housedrawba.adspackage.AppController.IN_APP_PURCHASE_KEY;
import static com.ba.housedrawba.adspackage.AppController.isInAppPurchased;
import static com.ba.housedrawba.adspackage.AppController.showRewardedAd;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.ba.housedrawba.Utils.SharedPreferenceManager;
import com.ba.housedrawba.activities.PreviewSavedActivity;
import com.ba.housedrawba.adspackage.AdUnifiedListening;
import com.ba.housedrawba.adspackage.AppController;
import com.ba.housedrawba.adspackage.SimpleBillingManager;
import com.ba.housedrawba.interfaces.RewardAdDismissedListener;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class BaseActivity extends AppCompatActivity implements SimpleBillingManager.OnBillingCallback {
    public SharedPreferenceManager sharedPreferenceManager;
    public String folderName;
//    public MyBillingManager billingManager;
    public SimpleBillingManager billingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UltimateBarX.statusBarOnly(this)
                .fitWindow(true)
                .transparent()
                .colorRes(android.R.color.transparent)
                .light(false)
                .apply();

        folderName = getApplicationContext().getResources().getString(R.string.folder_to_save_imgs);
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(), IN_APP_PURCHASE_KEY);
        billingManager = new SimpleBillingManager(this, this);
    }
    public void triggerFirebaseInAppEvent(Activity activity, String eventCategory) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(eventCategory,true);
        FirebaseAnalytics.getInstance(activity).logEvent("_"+eventCategory,bundle);
    }
    /*...........................................................*/
    public void showToast(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        new Handler().postDelayed(() -> {
            if (toast != null)
                toast.cancel();
        }, 1500);
        if (toast != null)
            toast.show();
    }
    public void showToast(String msg, Context context) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        new Handler().postDelayed(() -> {
            if (toast != null)
                toast.cancel();

        }, 1500);
        if (toast != null)
            toast.show();
    }

    public ArrayList<String> getHomeScreenDataList(Activity activity,String category) {
        ArrayList<String> stringList = new ArrayList<>();
        try {
            String[] list = activity.getResources().getAssets().list(category);
            if (list != null) {
                for (String s : list) {
                    String stringBuilder = "file:///android_asset/"+category+"/" + s;
                    stringList.add(stringBuilder);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return stringList;
    }
    //=====================================================================================================
    //    -------------------- AutoScrollOnClick ------------------------
    public void scrollToCenter(LinearLayoutManager layoutManager, RecyclerView recyclerList, int clickPosition) {
        RecyclerView.SmoothScroller smoothScroller = createSnapScroller(recyclerList, layoutManager);
        if (smoothScroller != null) {
            smoothScroller.setTargetPosition(clickPosition);
            layoutManager.startSmoothScroll(smoothScroller);
        }
    }
    private static final float MILLISECONDS_PER_INCH = 320f;
    private final static int DIMENSION = 2;
    private final static int HORIZONTAL = 0;
    private final static int VERTICAL = 1;
    @Nullable
    private LinearSmoothScroller createSnapScroller(RecyclerView mRecyclerView, RecyclerView.LayoutManager layoutManager) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return null;
        }
        return new LinearSmoothScroller(mRecyclerView.getContext()) {
            @Override
            protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
                int[] snapDistances = calculateDistanceToFinalSnap(layoutManager, targetView);
                final int dx = snapDistances[HORIZONTAL];
                final int dy = snapDistances[VERTICAL];
                final int time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)));
                if (time > 0) {
                    action.update(dx, dy, time, mDecelerateInterpolator);
                }
            }


            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }
        };
    }

    private int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,@NonNull View targetView) {
        int[] out = new int[DIMENSION];
        if (layoutManager.canScrollHorizontally()) {
            out[HORIZONTAL] = distanceToCenter(layoutManager, targetView,
                    OrientationHelper.createHorizontalHelper(layoutManager));
        }

        if (layoutManager.canScrollVertically()) {
            out[VERTICAL] = distanceToCenter(layoutManager, targetView,
                    OrientationHelper.createHorizontalHelper(layoutManager));
        }
        return out;
    }

    private int distanceToCenter(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView, OrientationHelper helper) {
        final int childCenter = helper.getDecoratedStart(targetView)
                + (helper.getDecoratedMeasurement(targetView) / 2);
        final int containerCenter;
        if (layoutManager.getClipToPadding()) {
            containerCenter = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
        } else {
            containerCenter = helper.getEnd() / 2;
        }
        return childCenter - containerCenter;
    }

    @Override
    public void onPurchaseSuccess() {

    }

    @Override
    public void onDialogDismiss() {

    }

    public void initNativeAdSingleSmallIntroScreen(@NonNull Context context, @NonNull TemplateView templateView) {
        NativeTemplateStyle style = new NativeTemplateStyle.Builder().build();
        templateView.setStyles(style);
        if (!isInAppPurchased) {
            AdRequest adRequest = new AdRequest.Builder().setHttpTimeoutMillis(6000).build();
            AppController.createUnifiedAds(context, R.string.ADMOB_NATIVE, adRequest, new AdUnifiedListening() {
                @Override
                public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                    Log.d("889_NativeAd_Single", "Normal Native loaded");
                    templateView.setNativeAd(nativeAd);
                    templateView.setVisibility(View.VISIBLE);
                }
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.e("889_NativeAd_Single", "Failed Normal Native: " + loadAdError.getMessage());
                }
            });
        }
    }

    public void showRewardedAdSingle(Activity activity, RewardAdDismissedListener dismissedListener) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.layout_dialog_interstitial_loader);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();

        if (!isInAppPurchased) {

            AdRequest adRequest = new AdRequest.Builder().setHttpTimeoutMillis(5000).build();
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d("441_mRewardedAdTag_", "Ad dismissed fullscreen content.");
                    if (dismissedListener != null) dismissedListener.onAdDismissed(true);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    // Called when ad fails to show.
                    if (dismissedListener != null) dismissedListener.onAdDismissed(false);
                    Log.e("441_mRewardedAdTag_", "Ad failed to show: ");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    AppController.isShowingInterstitialAd = true;
                    Log.d("441_mRewardedAdTag_", "Ad showed fullscreen content.");
                }
            };
            OnUserEarnedRewardListener onUserEarnedRewardListener = new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                }
            };

            RewardedAd.load(activity, activity.getString(R.string.ADMOB_REWARDED_HF), adRequest, new RewardedAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.e("441_mRewardedAdTag_", "Failed High: " + loadAdError.getMessage());

                    RewardedAd.load(activity, activity.getString(R.string.ADMOB_REWARDED_MF), adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e("441_mRewardedAdTag_", "Failed Medium: " + loadAdError.getMessage());
                            RewardedAd.load(activity, activity.getString(R.string.ADMOB_REWARDED), adRequest, new RewardedAdLoadCallback() {
                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    Log.e("441_mRewardedAdTag_", "Failed Normal: " + loadAdError.getMessage());
                                    if (dismissedListener != null) dismissedListener.onAdDismissed(false);
                                    dialog.dismiss();
                                    showToast("No Ad available please try again Later", activity);
                                }

                                @Override
                                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                                    Log.d("441_mRewardedAdTag_", "Normal Ad loaded.");
                                    dialog.dismiss();
                                    dismissedListener.onRewardAdLoaded();
                                    rewardedAd.setFullScreenContentCallback(fullScreenContentCallback);
                                    rewardedAd.show(activity, onUserEarnedRewardListener);

                                }
                            });
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            dialog.dismiss();
                            dismissedListener.onRewardAdLoaded();
                            rewardedAd.setFullScreenContentCallback(fullScreenContentCallback);
                            rewardedAd.show(activity, onUserEarnedRewardListener);
                            Log.d("441_mRewardedAdTag_", "High Medium Ad loaded.");
                        }
                    });
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    dialog.dismiss();
                    dismissedListener.onRewardAdLoaded();
                    rewardedAd.setFullScreenContentCallback(fullScreenContentCallback);
                    rewardedAd.show(activity, onUserEarnedRewardListener);
                    Log.d("441_mRewardedAdTag_", "High Reward Ad loaded.");
                }
            });
            /*if (spm_viewed_Ad_count.getIntValue(rw_ad_view_count_key, 1) < RW_AD_MAX_ADS){
            }else{
                showToast("You have reached max rewards limit try on next day");
            }*/
        }
    }

    //=====================================================================================================

    public interface onStoragePermissionGrantListener {
        void onStoragePerGranted();
    }

    public void requestStoragePermission(Activity activity, onStoragePermissionGrantListener listener) {
        Dexter.withContext(activity).withPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        listener.onStoragePerGranted();
                        Log.e("reqStorPermission_1", "- Granted -");
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        showSettingsDialog(activity);
                        Log.e("reqStorPermission_1", "- deneied -" + permissionDeniedResponse.getPermissionName());
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError dexterError) {
                        Log.e("reqStorPermission_1", "- error -" + dexterError.toString());
                    }
                })
                .check();
    }

    public boolean checkAppPermission(Activity activity, Bitmap.CompressFormat format, int quality, Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImageABOVE10(activity,bitmap,getString(R.string.folder_to_save_imgs),format,quality);
        } else {
            Dexter.withContext(activity)
                    .withPermissions(WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                saveSelectedImage(activity, getString(R.string.folder_to_save_imgs), bitmap,format,quality);
                            } else if (report.isAnyPermissionPermanentlyDenied()) {
                                showSettingsDialog(activity);
                            }
                        }
                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        }
        return false;
    }

    //..................................... Upload \ save image path ............................................
    public String saveSelectedImage(Activity activity, String dirName, Bitmap bitmap,Bitmap.CompressFormat format,int quality) {
        String pathFinal;
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.US).format(new Date());
        String fileName = "hd3" + timeStamp + ".png";

        String desFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File direct = new File(desFolder, "/" + dirName);

        if (!direct.exists()) {
            File wallpaperDirectory = new File(direct.toString());
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File(direct.toString()), fileName);
        if (file.exists()) file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
//            Bitmap bitmap = BitmapFactory.decodeFile();
            bitmap.compress(format, quality, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String pt = direct.toString() + "/" + fileName;
        pathFinal = pt;
        refreshGallery(activity, pt);

        if (!isInAppPurchased){
            showRewardedAd(activity, new AppController.InterstitialCustomAdListener() {
                @Override
                public void onAdClosedListener() {
                    activity.startActivity(new Intent(activity, PreviewSavedActivity.class).putExtra("asset_path", pt));
                }
                @Override
                public void onAdFailedToLoad(String errorMsg) {
                    activity.startActivity(new Intent(activity, PreviewSavedActivity.class).putExtra("asset_path", pt));
                }
            });
        }
        else {
            activity.startActivity(new Intent(activity, PreviewSavedActivity.class).putExtra("asset_path", pt));
        }

        return pathFinal;
    }
    public String saveImageABOVE10(Activity activity, Bitmap bitmap, String path,Bitmap.CompressFormat format, int quality) {
        String pathFinal = null;
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss",Locale.US).format(new Date());
        String fileName = "hd3" + timeStamp + ".png";
        OutputStream fos;
        String desFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver contentResolver = activity.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/*");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + path);


                Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                if (imageUri != null) {
                    fos = contentResolver.openOutputStream(imageUri);
                    bitmap.compress(format, quality, fos);
                    Toast.makeText(activity, "Saved in Gallery...", Toast.LENGTH_SHORT).show();
                    Objects.requireNonNull(fos);
                }
                String pp = File.separator + path;
                String filePath = desFolder + pp + "/" + fileName;
                pathFinal = filePath;
                refreshGallery(activity, filePath);

                if (!isInAppPurchased){
                    showRewardedAd(activity, new AppController.InterstitialCustomAdListener() {
                        @Override
                        public void onAdClosedListener() {
                            activity.startActivity(new Intent(activity, PreviewSavedActivity.class)
                                    .putExtra("asset_path", filePath)
                                    .putExtra("isShowAd", true)
                            );
                        }
                        @Override
                        public void onAdFailedToLoad(String errorMsg) {
                            activity.startActivity(new Intent(activity, PreviewSavedActivity.class)
                                    .putExtra("asset_path", filePath)
                                    .putExtra("isShowAd", true)
                            );
                        }
                    });
                }
                else {
                    activity.startActivity(new Intent(activity, PreviewSavedActivity.class)
                            .putExtra("asset_path", filePath)
                            .putExtra("isShowAd", true)
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pathFinal;
    }
    public void refreshGallery(Activity activity, String fileUri) {
        // Convert to file Object
        File file = new File(fileUri);
        // Write Kitkat version specific code for add entry to gallery database
        // Check for file existence
        if (file.exists()) {
            // Add / Move File
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(new File(fileUri));
            mediaScanIntent.setData(contentUri);
            activity.sendBroadcast(mediaScanIntent);
        } else {
            // Delete File
            try {
                activity.getContentResolver().delete(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Images.Media.DATA + "='"
                                + new File(fileUri).getPath() + "'", null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkAppPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    public boolean checkCameraAppPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
    public void requestCameraPermission(Activity activity, onStoragePermissionGrantListener listener) {
        Dexter.withContext(activity).withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        listener.onStoragePerGranted();
                        Log.e("reqStorPermission_1","- Granted -");
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        showToast("Please Allow Permission to take photos", activity);
                        Log.e("reqStorPermission_1","- deneied -"+permissionDeniedResponse.getPermissionName());
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError dexterError) {
                        Log.e("reqStorPermission_1","- error -"+dexterError.toString());
                    }
                })
                .check();
    }

    public static String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor = resolver.query(uri, null, null, null, null);
        if (returnCursor!=null && returnCursor.moveToFirst()){
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String name = returnCursor.getString(nameIndex);
            returnCursor.close();
            return name;
        }else {
            return null;
        }
    }

    public void showSettingsDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Grant Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings(activity);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }
    public void openSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, 101);
    }

    public Intent getCameraIntent(Activity activity, String fileName) {
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(activity, fileName));
    }
    public Intent getGalleryIntent() {
        return new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    public Uri getCacheImagePath(Activity activity, String fileName) {
        File path = new File(activity.getExternalCacheDir(), "camera");
        if (!path.exists())
            if (path.mkdirs()) {
                File image = new File(path, fileName);
                return getUriForFile(activity, activity.getPackageName() + ".provider", image);
            } else {
                return null;
            }
        else {
            File image = new File(path, fileName);
            return getUriForFile(activity, activity.getPackageName() + ".provider", image);
        }
    }
}