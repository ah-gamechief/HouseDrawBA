package com.ba.housedrawba.Utils.MyEditor.board;
import com.ba.housedrawba.Utils.MyEditor.element.DrawElement;
import com.ba.housedrawba.Utils.MyEditor.element.VirtualElement;
import java.util.ArrayList;
import java.util.List;
public class Selection {

    private final DrawElement element;

    Selection(DrawElement element) {
        this.element = element;
    }

    public boolean isEmptySelection() {
        return element == null;
    }

    public boolean isMultipleSelection() {
        return element instanceof VirtualElement;
    }

    public DrawElement getSingleElement() {
        if (isMultipleSelection()) {
            return null;
        } else {
            return element;
        }
    }

    public List<DrawElement> getElements() {
        if (isEmptySelection()) {
            return new ArrayList<>();
        }
        if (isMultipleSelection()) {
            VirtualElement virtualElement = (VirtualElement) element;
            return virtualElement.getElements();
        } else {
            List<DrawElement> elements = new ArrayList<>();
            elements.add(element);
            return elements;
        }
    }

}
