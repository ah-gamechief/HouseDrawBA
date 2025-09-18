package com.ba.housedrawba.Utils.MyEditor.view;
import android.content.Context;
public interface DrawingView {
    void setViewProxy(DrawingViewProxy viewProxy);
    void notifyViewUpdated();

    Context getContext();

}
