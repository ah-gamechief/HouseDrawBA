package com.ba.housedrawba.Utils.MyEditor.utils;
import com.ba.housedrawba.Utils.MyEditor.core.Vector2;

public final class VectorUtils {

    private VectorUtils() {}

    public static void shiftVectorAsSquare(Vector2 vector, boolean smallerFirst) {
        float deltaX = 0, deltaY = 0;
        float delta = Math.abs(Math.abs(vector.getValueX()) - Math.abs(vector.getValueY()));
        if (smallerFirst) {
            if (Math.abs(vector.getValueX()) < Math.abs(vector.getValueY())) {
                deltaY = vector.getValueY() > 0 ? -delta : delta;
            } else {
                deltaX = vector.getValueX() > 0 ? -delta : delta;
            }
        } else {
            if (Math.abs(vector.getValueX()) > Math.abs(vector.getValueY())) {
                deltaY = vector.getValueY() > 0 ? delta : -delta;
            } else {
                deltaX = vector.getValueX() > 0 ? delta : -delta;
            }
        }
        vector.getPoint2().x += deltaX;
        vector.getPoint2().y += deltaY;
    }

}
