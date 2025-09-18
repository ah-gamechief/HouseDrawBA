package com.ba.housedrawba.activities;
import static com.ba.housedrawba.adspackage.AppController.adClickCounter;
import static com.ba.housedrawba.adspackage.AppController.adsShowInterval;
import static com.ba.housedrawba.adspackage.AppController.showInterstitialAdWithLoader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.adspackage.AppController;
import com.ba.housedrawba.databinding.ActivityArDrawCameraBinding;
import com.bumptech.glide.Glide;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
public class ArDrawCameraActivity extends BaseActivity {
    ActivityArDrawCameraBinding binding;
    Activity activity = this;
    Camera camera;

    private CameraControl cameraControl;
    private static final String TAG = "ARDrawing";
    private Uri selectedImageUri; // Store the selected image URI

    final Matrix matrix = new Matrix();
    final float[] matrixValues = new float[9];
    private boolean isLocked = false;

    ActivityResultLauncher<PickVisualMediaRequest> launcher = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri == null) {
                        showToast("No image selected");
                    } else {
                        selectedImageUri = uri; // Store the selected image URI
                        binding.imageView.setImageURI(uri); // Set image in ImageView
                    }
                }
            });

    boolean isFlashOn = false; // Declare this at class level

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArDrawCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (adClickCounter>0 && adClickCounter%adsShowInterval==0)
            showInterstitialAdWithLoader(activity, new AppController.InterstitialCustomAdListener() {
                @Override
                public void onAdClosedListener() {

                }

                @Override
                public void onAdFailedToLoad(String errorMsg) {

                }
            });

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        cameraSetup();

        Glide.with(this).load(getIntent().getIntExtra("image", 0)).into(binding.imageView);
        String uriString = getIntent().getStringExtra("selectedImageUri");

        if (uriString != null) {
            Uri selectedImageUri = Uri.parse(uriString);
            // Load the image into ImageView using Glide
            Glide.with(this)
                    .load(selectedImageUri)
                    .into(binding.imageView);
        }

        binding.touchCapacity.setOnClickListener(v -> {
            binding.touchCapacity.setAlpha(0.5f);
            binding.flip.setAlpha(1.0f);
            binding.gallery.setAlpha(1.0f);
            binding.flash.setAlpha(1.0f);
            binding.lockAll.setAlpha(1.0f);
            if (binding.capacitylayout.getVisibility() == View.VISIBLE) {
                binding.capacitylayout.setVisibility(View.GONE);
                binding.touchCapacity.setAlpha(1.0f); // Reset alpha
            } else {
                binding.capacitylayout.setVisibility(View.VISIBLE);
                binding.touchCapacity.setAlpha(0.5f); // Dim button when active
            }
        });

        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String cameraIdWithFlash = null;

        try {
            for (String id : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
                Boolean hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                Integer lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (hasFlash != null && hasFlash && lensFacing != null &&
                        lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    cameraIdWithFlash = id;
                    break;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        String finalCameraId = cameraIdWithFlash;
        binding.flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                binding.flash.setAlpha(0.5f);
                binding.flip.setAlpha(1.0f);
                binding.touchCapacity.setAlpha(1.0f);
                binding.gallery.setAlpha(1.0f);
                binding.lockAll.setAlpha(1.0f);
                if (cameraControl != null) {
                    isFlashOn = !isFlashOn;
                    cameraControl.enableTorch(isFlashOn);

                    // Toggle alpha based on flash state
                    binding.flash.setAlpha(isFlashOn ? 0.5f : 1.0f);
                } else {
                    showToast("Camera control not available");
                }
            }
        });




        binding.capacitySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float alphaValue = (100 - progress) / 100f; // Invert progress: Right drag decreases transparency
                binding.imageView.setAlpha(alphaValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: Add actions when user starts dragging the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional: Add actions when user stops dragging the SeekBar
            }
        });
        binding.flip.setOnClickListener(new View.OnClickListener() {

            private int flipState = 0; // 0: Normal, 1: Horizontal, 2: Vertical, 3: Both

            @Override
            public void onClick(View v) {
                flipState = (flipState + 1) % 4; // Cycle through 0,1,2,3

                binding.flip.setAlpha(0.5f);
                binding.gallery.setAlpha(1.0f);
                binding.touchCapacity.setAlpha(1.0f);
                binding.flash.setAlpha(1.0f);
                binding.lockAll.setAlpha(1.0f);

                switch (flipState) {
                    case 0: // Normal
                        binding.imageView.setScaleX(1);
                        binding.imageView.setScaleY(1);
                        binding.flip.setAlpha(1.0f); // Full opacity
                        break;
                    case 1: // Flip Horizontally
                        binding.imageView.setScaleX(-1);
                        binding.imageView.setScaleY(1);
                        binding.flip.setAlpha(0.5f); // Dimmed
                        break;
                    case 2: // Flip Vertically
                        binding.imageView.setScaleX(1);
                        binding.imageView.setScaleY(-1);
                        binding.flip.setAlpha(0.5f);
                        break;
                    case 3: // Flip Both
                        binding.imageView.setScaleX(-1);
                        binding.imageView.setScaleY(-1);
                        binding.flip.setAlpha(0.5f);
                        break;
                }
            }
        });


        binding.gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.gallery.setAlpha(0.5f);
                binding.flip.setAlpha(1.0f);
                binding.touchCapacity.setAlpha(1.0f);
                binding.flash.setAlpha(1.0f);
                binding.lockAll.setAlpha(1.0f);


                openImagePicker();
            }
        });


        binding.lockAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isLocked = !isLocked;

                binding.lockAll.setAlpha(isLocked ? 0.5f : 1.0f);
                binding.flip.setAlpha(isLocked ? 0.2f : 1.0f);
                binding.touchCapacity.setAlpha(isLocked ? 0.2f : 1.0f);
                binding.flash.setAlpha(isLocked ? 0.2f : 1.0f);
                binding.gallery.setAlpha(isLocked ? 0.2f : 1.0f);
                binding.capacitylayout.setVisibility(isLocked ? View.GONE : View.VISIBLE);

                // Disable interaction with the views
                binding.flip.setEnabled(!isLocked);
                binding.touchCapacity.setEnabled(!isLocked);
                binding.flash.setEnabled(!isLocked);
                binding.gallery.setEnabled(!isLocked);
                binding.capacitySeekbar.setEnabled(!isLocked);

                // Keep the camera view (binding.previewView) active and visible
                // No action needed here since it's not being disabled
            }
        });




        binding.imageView.setScaleType(ImageView.ScaleType.MATRIX); // Important!

        final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();
                matrix.getValues(matrixValues);
                float currentScale = matrixValues[Matrix.MSCALE_X];

                float newScale = currentScale * scaleFactor;
                if (newScale > 6.0f) scaleFactor = 6.0f / currentScale; // Max zoom 6x
                if (newScale < 0.3f) scaleFactor = 0.3f / currentScale; // Min zoom 0.3x

                // Keep zoom centered
                float focusX = binding.imageView.getWidth() / 2f;
                float focusY = binding.imageView.getHeight() / 2f;

                matrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
                binding.imageView.setImageMatrix(matrix);
                return true;
            }
        });

        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                matrix.postTranslate(-distanceX, -distanceY);
                binding.imageView.setImageMatrix(matrix);
                return true;
            }
        });

        binding.imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isLocked) return false; // Ignore touch when locked
                scaleGestureDetector.onTouchEvent(event);
                gestureDetector.onTouchEvent(event);
                return true; // Important to return true
            }
        });


    }
    private void cameraSetup() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Unbind previous use cases before rebinding
                cameraProvider.unbindAll();

                // Select back camera as default
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Preview use case
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

                // Image analysis (Optional: For AR-based tracking)
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                // Bind camera to lifecycle
                camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageAnalysis);

                // Get Camera Control
                cameraControl = camera.getCameraControl();

            } catch (Exception e) {
                Log.e(TAG, "Camera setup failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }
    private void openImagePicker() {
        launcher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }
    private Bitmap convertUriToBitmap(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private Bitmap convertToTracingEffect(Bitmap originalBitmap) {
        if (originalBitmap == null) return null;

        // Convert to grayscale
        Bitmap grayBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0); // Remove color (convert to grayscale)
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(originalBitmap, 0, 0, paint);

        // Apply edge detection (basic thresholding for tracing effect)
        Bitmap tracingBitmap = Bitmap.createBitmap(grayBitmap.getWidth(), grayBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        for (int x = 0; x < grayBitmap.getWidth(); x++) {
            for (int y = 0; y < grayBitmap.getHeight(); y++) {
                int pixel = grayBitmap.getPixel(x, y);
                int red = Color.red(pixel);
                int threshold = 128; // Adjust this value to make lines darker or lighter

                if (red > threshold) {
                    tracingBitmap.setPixel(x, y, Color.BLACK); // Light areas
                } else {
                    tracingBitmap.setPixel(x, y, Color.TRANSPARENT); // Dark areas
                }
            }
        }

        return tracingBitmap;

    }
}