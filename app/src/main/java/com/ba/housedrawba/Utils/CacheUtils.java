package com.ba.housedrawba.Utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
public class CacheUtils {
    private static final String CACHE_FOLDER_NAME = "SketchToAi_";

    // Save two bitmaps (with and without watermark)
    public static void saveBitmapPairToCache(Context context, Bitmap withWatermark, Bitmap withoutWatermark) {
        long timestamp = System.currentTimeMillis();
        File cacheDir = new File(context.getCacheDir(), CACHE_FOLDER_NAME);

        Log.d(CACHE_FOLDER_NAME, "Cache directory path: " + cacheDir.getAbsolutePath());

        if (!cacheDir.exists()) {
            boolean created = cacheDir.mkdirs();
            Log.d(CACHE_FOLDER_NAME, "Cache directory created: " + created);
        }

        File fileWith = new File(cacheDir, timestamp + "_with.png");
        File fileWithout = new File(cacheDir, timestamp + "_without.png");

        try {
            FileOutputStream fosWith = new FileOutputStream(fileWith);
            withWatermark.compress(Bitmap.CompressFormat.PNG, 100, fosWith);
            fosWith.close();
            Log.d(CACHE_FOLDER_NAME, "Saved with-watermark file: " + fileWith.getAbsolutePath());

            FileOutputStream fosWithout = new FileOutputStream(fileWithout);
            withoutWatermark.compress(Bitmap.CompressFormat.PNG, 100, fosWithout);
            fosWithout.close();
            Log.d(CACHE_FOLDER_NAME, "Saved without-watermark file: " + fileWithout.getAbsolutePath());

        } catch (Exception e) {
            Log.e(CACHE_FOLDER_NAME, "Bitmap saving failed: " + e.getMessage(), e);
        }
    }

    // Model class
    public static class BitmapPair {
        public Bitmap withWatermark;
        public Bitmap withoutWatermark;
        public String pathWith;
        public String pathWithout;

        public BitmapPair(Bitmap withWatermark, Bitmap withoutWatermark, String pathWith, String pathWithout) {
            this.withWatermark = withWatermark;
            this.withoutWatermark = withoutWatermark;
            this.pathWith = pathWith;
            this.pathWithout = pathWithout;
        }
    }

    // Retrieve all bitmap pairs
    public static List<BitmapPair> getAllBitmapPairs(Context context) {
        List<BitmapPair> list = new ArrayList<>();
        File cacheDir = new File(context.getCacheDir(), CACHE_FOLDER_NAME);

        Log.d(CACHE_FOLDER_NAME, "Loading cache from: " + cacheDir.getAbsolutePath());

        if (!cacheDir.exists()) {
            Log.d(CACHE_FOLDER_NAME, "Cache directory does not exist.");
            return list;
        }

        File[] files = cacheDir.listFiles();
        if (files == null || files.length == 0) {
            Log.d(CACHE_FOLDER_NAME, "Cache directory is empty.");
            return list;
        }

        Arrays.sort(files, Comparator.comparing(File::getName));

        for (File file : files) {
            String name = file.getName();
            if (name.endsWith("_with.png")) {
                String baseName = name.replace("_with.png", "");
                File fileWithout = new File(cacheDir, baseName + "_without.png");

                if (fileWithout.exists()) {
                    Log.d(CACHE_FOLDER_NAME, "Found pair: " + file.getName() + " & " + fileWithout.getName());

                    Bitmap withBmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                    Bitmap withoutBmp = BitmapFactory.decodeFile(fileWithout.getAbsolutePath());

                    if (withBmp != null && withoutBmp != null) {
                        list.add(new BitmapPair(withBmp, withoutBmp, file.getAbsolutePath(), fileWithout.getAbsolutePath()));
                    } else {
                        Log.w(CACHE_FOLDER_NAME, "One of the bitmaps could not be decoded: " + baseName);
                    }
                } else {
                    Log.w(CACHE_FOLDER_NAME, "Missing without-watermark file for: " + baseName);
                }
            }
        }
        Log.d(CACHE_FOLDER_NAME, "Total pairs loaded: " + list.size());
        return list;
    }

    // Check if cache is empty
    public static boolean isCacheEmpty(Context context) {
        File cacheDir = new File(context.getCacheDir(), CACHE_FOLDER_NAME);
        File[] files = cacheDir.listFiles();
        boolean empty = (files == null || files.length == 0);
        Log.d(CACHE_FOLDER_NAME, "Cache empty: " + empty);
        return empty;
    }
}
