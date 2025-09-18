package com.ba.housedrawba.Utils.MyEditor.mode.selection;
import android.graphics.Path;
import android.view.MotionEvent;

import com.ba.housedrawba.Utils.MyEditor.core.Vector2;
import com.ba.housedrawba.Utils.MyEditor.element.shape.ShapeElement;
public abstract class ShapeSelectionMode extends SelectionMode {

    public ShapeSelectionMode() {}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (selectionElement instanceof ShapeElement) {
                ((ShapeElement) selectionElement).setupElementByVector(new Vector2(downX, downY, event.getX(), event.getY()));
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected Path getSelectionPath() {
        if (selectionElement instanceof ShapeElement) {
            return ((ShapeElement) selectionElement).getElementPath();
        } else {
            return null;
        }
    }

}
