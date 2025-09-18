package com.ba.housedrawba.Utils.MyEditor.view;
import android.graphics.Canvas;
import android.view.MotionEvent;

import com.ba.housedrawba.Utils.MyEditor.DrawingContext;
import com.ba.housedrawba.Utils.MyEditor.board.ElementManager;
import com.ba.housedrawba.Utils.MyEditor.element.DrawElement;
public class DrawingViewProxyImpl implements DrawingViewProxy {

    private final DrawingView drawingView;
    private final DrawingContext context;
    private final ElementManager elementManager;

    public DrawingViewProxyImpl(DrawingView drawingView, DrawingContext context, ElementManager elementManager) {
        this.drawingView = drawingView;
        this.context = context;
        this.elementManager = elementManager;
    }

    @Override
    public void onDraw(Canvas canvas) {
        DrawElement[] elements = elementManager.getVisibleObjects();
        for (DrawElement element : elements) {
            element.drawToCanvas(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (context.getDrawingMode() != null) {
            boolean result = context.getDrawingMode().onTouchEvent(event);
            if (result) {
                if (drawingView!=null) drawingView.notifyViewUpdated();
            }
            return result;
        }
        return false;
    }
}
