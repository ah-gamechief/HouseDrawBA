package com.ba.housedrawba.Utils.MyEditor.utils;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;

public final class ShapeUtils {

    private ShapeUtils() {
    }


    public static RectF createSquare(float centerX, float centerY, float radius) {
        return new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
    }


    public static float calculateRotationDegree(PointF center, PointF p1, PointF p2) {
        double angle1 = Math.atan2(p1.y - center.y, p1.x - center.x);
        double angle2 = Math.atan2(p2.y - center.y, p2.x - center.x);
        float angle = (float) Math.toDegrees(angle2 - angle1);
        if (angle > 180) {
            angle -= 360;
        } else if (angle < -180) {
            angle += 360;
        }
        return angle;
    }


    public static Region createRegionFromPath(Path path) {
        Region region = new Region();
        if (path != null) {
            RectF box = new RectF();
            path.computeBounds(box, true);
            region.setPath(path, new Region((int) box.left, (int) box.top, (int) box.right, (int) box.bottom));
        }
        return region;
    }

}
