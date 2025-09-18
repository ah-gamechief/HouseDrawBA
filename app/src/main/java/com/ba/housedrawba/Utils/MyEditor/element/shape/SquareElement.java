package com.ba.housedrawba.Utils.MyEditor.element.shape;


import com.ba.housedrawba.Utils.MyEditor.core.Vector2;
import com.ba.housedrawba.Utils.MyEditor.utils.VectorUtils;

public class SquareElement extends RectElement {

    @Override
    public void setupElementByVector(Vector2 vector) {
        VectorUtils.shiftVectorAsSquare(vector, true);
        super.setupElementByVector(vector);
    }

    @Override
    public boolean isLockAspectRatio() {
        return true;
    }

}
