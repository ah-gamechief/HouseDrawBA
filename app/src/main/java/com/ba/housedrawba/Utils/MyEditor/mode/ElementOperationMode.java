package com.ba.housedrawba.Utils.MyEditor.mode;
import android.view.MotionEvent;

import com.ba.housedrawba.Utils.MyEditor.PaintBuilder;
import com.ba.housedrawba.Utils.MyEditor.board.ElementManager;
import com.ba.housedrawba.Utils.MyEditor.core.CiPaint;
import com.ba.housedrawba.Utils.MyEditor.element.DrawElement;
import com.ba.housedrawba.Utils.MyEditor.operation.OperationManager;
public class ElementOperationMode extends AbstractDrawingMode {

    protected PaintBuilder paintBuilder;
    protected ElementManager elementManager;
    protected OperationManager operationManager;
    
    protected DrawElement element;
    protected CiPaint originalPaint;
    protected CiPaint previewPaint;

    public ElementOperationMode() {
    }

    public DrawElement getElement() {
        return element;
    }

    public void setElement(DrawElement element) {
        this.element = element;
        if (element != null) {
            previewPaint = paintBuilder.createPreviewPaint(element.getPaint());
        }
    }

    @Override
    public void setDrawingBoardId(String boardId) {
        super.setDrawingBoardId(boardId);
        paintBuilder = drawingBoard.getPaintBuilder();
        elementManager = drawingBoard.getElementManager();
        operationManager = drawingBoard.getOperationManager();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (element != null) {
                    originalPaint = new CiPaint(element.getPaint());
                    element.setPaint(previewPaint);
                    return true;
                }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (element != null) {
                    element.setPaint(originalPaint);
                    return true;
                }
        }
        return false;
    }

}
