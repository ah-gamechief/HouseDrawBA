package com.ba.housedrawba.Utils.MyEditor.element.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

import com.ba.housedrawba.Utils.MyEditor.core.Vector2;
import com.ba.housedrawba.Utils.MyEditor.element.BasePathElement;
import com.ba.housedrawba.Utils.MyEditor.element.behavior.SupportVector;

public abstract class ShapeElement extends BasePathElement implements SupportVector {

    public ShapeElement() {
    }

    @Override
    public void afterLoaded() {
        elementPath = createShapePath();
        updateBoundingBox();

    }

    @Override
    public void drawElement(Canvas canvas) {
        if (elementPath != null) {
            canvas.drawPath(elementPath, paint);
            if (paint.getStyle() == Paint.Style.FILL_AND_STROKE) {
                Integer originalColor = null;
                if (paint.getSecondaryColor() != null) {
                    originalColor = paint.getColor();
                    paint.setColor(paint.getSecondaryColor());
                }
                paint.setStyle(Paint.Style.FILL);
                paint.setAntiAlias(true);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setStrokeJoin(Paint.Join.ROUND);
                canvas.drawPath(elementPath, paint);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (originalColor != null) {
                    paint.setColor(originalColor);
                }
            }
        }
    }

    @Override
    public void setupElementByVector(Vector2 vector) {
        retrieveAttributesFromVector(vector);
        elementPath = createShapePath();
        updateBoundingBox();
    }

    @Override
    public void applyMatrixForData(Matrix matrix) {
        super.applyMatrixForData(matrix);

        elementPath = createShapePath();
        elementPath.transform(dataMatrix);
    }

    protected abstract void retrieveAttributesFromVector(Vector2 vector);

    protected abstract Path createShapePath();

}
