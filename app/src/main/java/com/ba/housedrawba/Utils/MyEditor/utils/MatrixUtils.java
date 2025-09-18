package com.ba.housedrawba.Utils.MyEditor.utils;
import android.graphics.Matrix;
public final class MatrixUtils {

    private MatrixUtils() {}


    public static Matrix getInvertMatrix(Matrix matrix) {
        Matrix invertMatrix = new Matrix();
        matrix.invert(invertMatrix);
        return invertMatrix;
    }


    public static Matrix getTransformationMatrix(Matrix source, Matrix target) {
        Matrix delta = new Matrix();
        source.invert(delta);
        delta.postConcat(target);
        return delta;
    }
}
