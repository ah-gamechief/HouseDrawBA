package com.ba.housedrawba.Utils.MyEditor.mode;

import android.util.Log;
import android.view.MotionEvent;

import com.ba.housedrawba.Utils.MyEditor.CircleLog;
import com.ba.housedrawba.Utils.MyEditor.DrawingContext;
import com.ba.housedrawba.Utils.MyEditor.PaintBuilder;
import com.ba.housedrawba.Utils.MyEditor.board.ElementManager;
import com.ba.housedrawba.Utils.MyEditor.core.CiPaint;
import com.ba.housedrawba.Utils.MyEditor.core.Vector2;
import com.ba.housedrawba.Utils.MyEditor.element.DrawElement;
import com.ba.housedrawba.Utils.MyEditor.element.PhotoElement;
import com.ba.housedrawba.Utils.MyEditor.element.behavior.SupportVector;
import com.ba.housedrawba.Utils.MyEditor.element.shape.RectElement;
import com.ba.housedrawba.Utils.MyEditor.operation.InsertElementOperation;
import com.ba.housedrawba.Utils.MyEditor.operation.OperationManager;

public class InsertVectorElementMode extends AbstractDrawingMode {

    private static final String TAG = "InsertVectorElementMode";

    protected ElementManager elementManager;
    protected DrawingContext drawingContext;
    protected OperationManager operationManager;
    protected PaintBuilder paintBuilder;

    protected float downX;
    protected float downY;

    protected DrawElement previewElement;
    protected DrawElement realElement;

    public InsertVectorElementMode() {}

    @Override
    public void setDrawingBoardId(String boardId) {
        super.setDrawingBoardId(boardId);
        elementManager = drawingBoard.getElementManager();
        drawingContext = drawingBoard.getDrawingContext();
        operationManager = drawingBoard.getOperationManager();
        paintBuilder = drawingBoard.getPaintBuilder();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                CircleLog.d(TAG, "Touch Down: x=" + downX + ", y=" + downY);

                previewElement = createPreviewElement();
                elementManager.addElementToCurrentLayer(previewElement);

                return true;
            case MotionEvent.ACTION_MOVE:
                if (previewElement != null) {
                    ((SupportVector) previewElement).setupElementByVector(new Vector2(downX, downY, event.getX(), event.getY()));
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (previewElement != null) {
                    elementManager.removeElementFromCurrentLayer(previewElement);
//                    if (createRealElement(new Vector2(0, 0, 512, 980))!=null) {
//                        DrawElement element = createRealElement(new Vector2(0, 0, 512, 980));
//                    if (element instanceof PhotoElement){
//                        Log.e("PhotoElement_02","Its Photo man its Photo");
//                    }
//                        if (element.getName() != null) {
//                            Log.e("001_InsetVector__", "it's text being added");
//                        int mWidth= this.getResources().getDisplayMetrics().widthPixels;
//                        int mHeight= this.getResources().getDisplayMetrics().heightPixels;
//                        element = createRealElement(new Vector2(downX, downY, event.getX(), event.getY()));
//                        } else {
//                            Log.e("001_InsetVector__", "Not working");
//                        }
//                    }
                    DrawElement element = createRealElement(new Vector2(downX, downY, event.getX(), event.getY()));
                    if (element!=null) operationManager.executeOperation(new InsertElementOperation(element));
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
                elementManager.removeElementFromCurrentLayer(previewElement);
                return true;
        }
        return false;
    }

    protected void setVectorElement(DrawElement realElement) {
        this.realElement = realElement;
        if (!(realElement instanceof SupportVector)) {
            throw new IllegalArgumentException("Element must implement CreateByVector interface.");
        }
        if (realElement instanceof PhotoElement){
            Log.e("PhotoElement_01","Its Photo man its Photo");
            if (createRealElement(new Vector2(0, 0, 300, 600))!=null) {
                DrawElement element = createRealElement(new Vector2(0, 0, 300, 600));
                operationManager.executeOperation(new InsertElementOperation(element));
            }
        }
    }

    protected void setVectorElement(DrawElement realElement,int x, int y) {
        if (!(realElement instanceof SupportVector)) {
            throw new IllegalArgumentException("Element must implement CreateByVector interface.");
        }
        if (realElement instanceof PhotoElement){
            this.realElement = realElement;
            Log.e("PhotoElement_01","Its Photo man its Photo");
            if (createRealElement(new Vector2(x-150, y-150, x+150, y+150))!=null) {
                DrawElement element = createRealElement(new Vector2(x - 150, y - 150, x + 150, y + 150));
                operationManager.executeOperation(new InsertElementOperation(element));
            }
        }
    }
    protected DrawElement createPreviewElement() {
        previewElement = new RectElement();
        previewElement.setPaint(paintBuilder.createPreviewAreaPaint(drawingContext.getPaint()));
        return previewElement;
    }
    protected DrawElement createRealElement(Vector2 vector) {
        DrawElement element = (DrawElement) realElement.clone();
        element.setPaint(new CiPaint(drawingContext.getPaint()));
        ((SupportVector) element).setupElementByVector(vector);
        return element;
    }

}
