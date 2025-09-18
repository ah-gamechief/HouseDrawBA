package com.ba.housedrawba.Utils.MyEditor;
public interface ConfigManager {

    enum DrawingType {
        Vector,
        Painting
    }


    boolean isDebugMode();


    void setDebugMode(boolean debugMode);


    DrawingType getDrawingType();


    void setDrawingType(DrawingType type);

}
