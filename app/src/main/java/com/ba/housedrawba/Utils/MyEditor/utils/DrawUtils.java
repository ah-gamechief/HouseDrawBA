package com.ba.housedrawba.Utils.MyEditor.utils;
import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
public final class DrawUtils {

    private DrawUtils() {
    }


    public static boolean isSingleTap(Context context, float downX, float downY, MotionEvent upEvent) {
        return isSingleTap(context, upEvent.getDownTime(), upEvent.getEventTime(), downX, downY, upEvent.getX(), upEvent.getY());
    }


    public static boolean isSingleTap(Context context, long downTime, long upTime, float downX, float downY, float upX, float upY) {
        if (upTime - downTime > ViewConfiguration.getTapTimeout()) {
            return false;
        }
        int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        final int deltaX = (int) (downX - upX);
        final int deltaY = (int) (downY - upY);
        int distance = (deltaX * deltaX) + (deltaY * deltaY);
        return distance <= touchSlop * touchSlop;
    }


    public static Rect createTouchSquare(Context context, int x, int y) {
        int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        return new Rect(x - touchSlop, y - touchSlop, x + touchSlop, y + touchSlop);
    }
}
