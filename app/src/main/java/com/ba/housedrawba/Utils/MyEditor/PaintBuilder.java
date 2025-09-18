package com.ba.housedrawba.Utils.MyEditor;
import com.ba.housedrawba.Utils.MyEditor.core.CiPaint;

public interface PaintBuilder {


    CiPaint createDebugPaintForLine();


    CiPaint createDebugPaintForArea();


    CiPaint createPreviewPaint(CiPaint originalPaint);


    CiPaint createPreviewAreaPaint(CiPaint originalPaint);


    CiPaint createRectSelectionToolPaint();


    CiPaint createSelectionBoundPaint();


    CiPaint createSelectionAreaPaint();


    CiPaint createResizingHandlePaint();


    CiPaint createRotationHandlePaint();


    CiPaint createReferencePointPaint();

}
