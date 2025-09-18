package com.ba.housedrawba.Utils.MyEditor.mode;
import android.view.MotionEvent;
public class AutoDetectedElementOperationMode extends ElementOperationMode {

    protected boolean autoDetectMode;

    public AutoDetectedElementOperationMode() {}

    public AutoDetectedElementOperationMode(boolean autoDetectMode) {
        this.autoDetectMode = autoDetectMode;
    }

    public boolean isAutoDetectMode() {
        return autoDetectMode;
    }

    public void setAutoDetectMode(boolean autoDetectMode) {
        this.autoDetectMode = autoDetectMode;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (autoDetectMode) {
                detectElement(event.getX(), event.getY());
            }
        }
        return super.onTouchEvent(event);
    }

    protected void detectElement(float x, float y) {
        setElement(elementManager.getFirstHitElement(x, y));
    }

}
