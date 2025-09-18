package com.ba.housedrawba.Utils.CustomViews.Rubber;
import static com.ba.housedrawba.Utils.CustomViews.Rubber.RubberStampPosition.CUSTOM;
import static com.ba.housedrawba.Utils.CustomViews.Rubber.RubberStampPosition.TILE;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;
public class RubberStamp {
    private final Context mContext;
    private static final int BACKGROUND_MARGIN = 10;
    
    public RubberStamp(@NonNull Context context){
        mContext = context;
    }

    public Bitmap addStamp(@NonNull RubberStampConfig config) {
        if (config == null) throw new IllegalArgumentException("The config passed to this method should never"+"be null");
        Bitmap baseBitmap = getBaseBitmap(config);
        if (baseBitmap == null) return baseBitmap;
        int baseBitmapWidth = baseBitmap.getWidth();
        int baseBitmapHeight = baseBitmap.getHeight();
      
        Bitmap result = Bitmap.createBitmap(baseBitmapWidth, baseBitmapHeight, Objects.requireNonNull(baseBitmap.getConfig()));
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(baseBitmap, 0, 0, null);

        if (!TextUtils.isEmpty(config.getRubberStampString())) {
            addTextToBitmap(config, canvas, baseBitmapWidth, baseBitmapHeight);
        } else if (config.getRubberStampBitmap() != null) {
            addBitmapToBitmap(config.getRubberStampBitmap(), config, canvas,
                    baseBitmapWidth, baseBitmapHeight);
        }
        return result;
    }
  
    @Nullable
    private Bitmap getBaseBitmap(@NonNull RubberStampConfig config) {
        Bitmap baseBitmap = config.getBaseBitmap();
        @DrawableRes int drawable = config.getBaseDrawable();
        if (baseBitmap == null) {
            baseBitmap = BitmapFactory.decodeResource(mContext.getResources(), drawable);
        }
        return baseBitmap;
    }

    private void addTextToBitmap(@NonNull RubberStampConfig config,@NonNull Canvas canvas,
                                 int baseBitmapWidth, int baseBitmapHeight) {
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setUnderlineText(false);
        paint.setTextSize(config.getTextSize());
        String typeFacePath = config.getTypeFacePath();
        // Add font typeface if its present in the config.
        if(!TextUtils.isEmpty(typeFacePath)) {
            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), typeFacePath);
            paint.setTypeface(typeface);
        }
        Shader shader = config.getTextShader();
        // Add shader if its present in the config.
        if (shader != null) paint.setShader(shader);
        if (config.getTextShadowXOffset() != 0 || config.getTextShadowYOffset() != 0 || config.getTextShadowBlurRadius() != 0) {
            // If any shadow property is present, set a shadow layer.
            paint.setShadowLayer(config.getTextShadowBlurRadius(),
                    config.getTextShadowXOffset(),
                    config.getTextShadowYOffset(),
                    config.getTextShadowColor());
        }

        String rubberStampString = config.getRubberStampString();
        paint.getTextBounds(rubberStampString,0,rubberStampString.length(),bounds);

        int rubberStampWidth = bounds.width();
        float rubberStampMeasuredWidth = paint.measureText(rubberStampString);
        int rubberStampHeight = bounds.height();

        int positionX = config.getPositionX();
        int positionY = config.getPositionY();

        if (config.getRubberStampPosition() != CUSTOM) {
            // If the specified RubberStampPosition is not CUSTOM, use calculates its x & y
            // co-ordinates.
            Pair<Integer, Integer> pair = PositionCalculator
                    .getCoordinates(config.getRubberStampPosition(),
                            baseBitmapWidth, baseBitmapHeight,
                            rubberStampWidth, rubberStampHeight);
            positionX = pair.first;
            positionY = pair.second;
        }

        // Add the margin to this position if it was passed to the config.
        positionX += config.getXMargin();
        positionY += config.getYMargin();

        float rotation = config.getRotation();
        // Add rotation if its present in the config.
        if (rotation != 0.0f) canvas.rotate(rotation, positionX + bounds.exactCenterX(), positionY - bounds.exactCenterY());
        paint.setColor(config.getTextColor());
        int alpha = config.getAplha();
        if (alpha >= 0 && alpha <= 255) paint.setAlpha(alpha);

        if (config.getRubberStampPosition() != TILE) {
            int backgroundColor = config.getTextBackgroundColor();
            if (backgroundColor != 0) {
                Paint backgroundPaint = new Paint();
                backgroundPaint.setColor(backgroundColor);
                canvas.drawRect(positionX - BACKGROUND_MARGIN,
                        positionY - bounds.height() - paint.getFontMetrics().descent - BACKGROUND_MARGIN,
                        (positionX + rubberStampMeasuredWidth + config.getTextShadowXOffset() + BACKGROUND_MARGIN),
                        positionY + config.getTextShadowYOffset() + paint.getFontMetrics().descent + BACKGROUND_MARGIN,
                        backgroundPaint);
            }
            canvas.drawText(rubberStampString, positionX , positionY, paint);
        } else {
            // Add vertical margin (e.g., 20px)
            int verticalMargin = 180;
            Bitmap textImage = Bitmap.createBitmap((int) rubberStampMeasuredWidth,
                    rubberStampHeight + verticalMargin, Bitmap.Config.ARGB_8888);

//            Bitmap textImage = Bitmap.createBitmap((int)rubberStampMeasuredWidth,
//                    rubberStampHeight, Bitmap.Config.ARGB_8888);
            Canvas textCanvas = new Canvas(textImage);
            paint.setFakeBoldText(true);
            textCanvas.drawText(config.getRubberStampString(), 0, rubberStampHeight-10, paint);
            paint.setShader(new BitmapShader(textImage,
                    Shader.TileMode.REPEAT,
                    Shader.TileMode.REPEAT));
            Rect bitmapShaderRect = canvas.getClipBounds();
            canvas.drawRect(bitmapShaderRect, paint);
        }    
    }

    private void addBitmapToBitmap(@NonNull Bitmap rubberStampBitmap, @NonNull RubberStampConfig config,
                                   @NonNull Canvas canvas, int baseBitmapWidth, int baseBitmapHeight) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setUnderlineText(false);

        ColorFilter filter = new PorterDuffColorFilter(
                config.getTextColor()
//                ContextCompat.getColor(mContext, config.getTextColor())
                , PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);

        int alpha = config.getAplha();
        if (alpha >= 0 && alpha <= 255) paint.setAlpha(alpha);

        int positionX = config.getPositionX()/4;
        int positionY = config.getPositionY()/4;
        RubberStampPosition rubberStampPosition = config.getRubberStampPosition();
        if (rubberStampPosition != CUSTOM) {
            Pair<Integer, Integer> pair =
                    PositionCalculator.getCoordinates(rubberStampPosition,
                            baseBitmapWidth, baseBitmapHeight,
                            rubberStampBitmap.getWidth(), rubberStampBitmap.getHeight());
            positionX = pair.first;
            positionY = pair.second - rubberStampBitmap.getHeight();
        }
        // Add the margin to this position if it was passed to the config.
        positionX += config.getXMargin();
        positionY += config.getYMargin();

        float rotation = config.getRotation();
        if (rotation != 0.0f) {
            // Add rotation if its present in the config.
            canvas.rotate(rotation, positionX + (rubberStampBitmap.getWidth() / 2f),
                    positionY + (rubberStampBitmap.getHeight() / 2f));
        }
        if (rubberStampPosition != TILE) {
            canvas.drawBitmap(rubberStampBitmap, positionX, positionY , paint);
        } else {
            // If the specified RubberStampPosition is TILE, it tiles the rubberstamp across
            // the bitmap. In order to generate a tiled bitamp, it uses a bitmap shader.
            paint.setShader(new BitmapShader(rubberStampBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
            Rect bitmapShaderRect = canvas.getClipBounds();
            canvas.drawRect(bitmapShaderRect, paint);
//            Rect src = new Rect(0, 0, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
//            Rect dest = new Rect(0, 0, width - 1, height - 1);
//            canvas.drawBitmap(mBackground, src, dest, null);
        }
    }
}
