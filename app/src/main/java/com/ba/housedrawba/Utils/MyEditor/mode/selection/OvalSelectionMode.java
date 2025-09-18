package com.ba.housedrawba.Utils.MyEditor.mode.selection;
import com.ba.housedrawba.Utils.MyEditor.element.DrawElement;
import com.ba.housedrawba.Utils.MyEditor.element.shape.OvalElement;
public class OvalSelectionMode extends ShapeSelectionMode {

    public OvalSelectionMode() {}

    @Override
    protected DrawElement createSelectionElement() {
        return new OvalElement();
    }

}
