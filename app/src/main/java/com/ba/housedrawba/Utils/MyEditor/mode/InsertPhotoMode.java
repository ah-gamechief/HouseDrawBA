package com.ba.housedrawba.Utils.MyEditor.mode;
import com.ba.housedrawba.Utils.MyEditor.element.PhotoElement;

public class InsertPhotoMode extends InsertVectorElementMode {

    public InsertPhotoMode() {}

    public void setPhotoElement(PhotoElement element) {
        setVectorElement(element);
    }
    public void setPhotoElement(PhotoElement element,int x, int y) {
        setVectorElement(element,x,y);
    }

}
