package com.ba.housedrawba.Utils.MyEditor.mode.stroke;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.ba.housedrawba.Utils.MyEditor.core.CiPaint;
public class EraserStrokeMode extends BaseStrokeMode {

    @Override
    protected CiPaint assignPaint() {
        CiPaint paint = new CiPaint(drawingContext.getPaint());
        paint.setStyle(Paint.Style.STROKE);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        return paint;
    }
}
