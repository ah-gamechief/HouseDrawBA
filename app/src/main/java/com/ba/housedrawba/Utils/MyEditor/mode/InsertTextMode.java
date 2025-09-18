package com.ba.housedrawba.Utils.MyEditor.mode;


import com.ba.housedrawba.Utils.MyEditor.element.TextElement;

public class InsertTextMode extends InsertVectorElementMode {

    public InsertTextMode() {}

    public void setTextElement(TextElement element) {
        setVectorElement(element);
    }

}
