package com.ba.housedrawba.Utils.MyEditor.mode.stroke;
import android.graphics.Paint;

import com.ba.housedrawba.Utils.MyEditor.core.CiPaint;
public class PlainStrokeMode extends BaseStrokeMode {

    public PlainStrokeMode() {}

    protected CiPaint assignPaint() {
        CiPaint paint = new CiPaint(drawingContext.getPaint());
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }
}
