package com.ba.housedrawba.Utils.MyEditor.mode.selection;
import com.ba.housedrawba.Utils.MyEditor.element.DrawElement;
import com.ba.housedrawba.Utils.MyEditor.element.shape.RectElement;
public class RectSelectionMode extends ShapeSelectionMode {

    public RectSelectionMode() {}

    @Override
    protected DrawElement createSelectionElement() {
        return new RectElement();
    }

}
