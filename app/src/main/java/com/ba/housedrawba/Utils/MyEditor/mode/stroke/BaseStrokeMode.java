package com.ba.housedrawba.Utils.MyEditor.mode.stroke;
import com.ba.housedrawba.Utils.MyEditor.DrawingContext;
import com.ba.housedrawba.Utils.MyEditor.board.ElementManager;
import com.ba.housedrawba.Utils.MyEditor.core.CiPaint;
import com.ba.housedrawba.Utils.MyEditor.element.StrokeElement;
import com.ba.housedrawba.Utils.MyEditor.mode.BasePointMode;
import com.ba.housedrawba.Utils.MyEditor.operation.InsertElementOperation;
import com.ba.housedrawba.Utils.MyEditor.operation.OperationManager;
public abstract class BaseStrokeMode extends BasePointMode {

    protected ElementManager elementManager;
    protected OperationManager operationManager;
    protected DrawingContext drawingContext;

    protected StrokeElement element;
    protected boolean immutable;

    public BaseStrokeMode() {}

    public boolean getStrokeImmutable() {
        return immutable;
    }

    public void setStrokeImmutable(boolean immutable) {
        this.immutable = immutable;
        if (element != null) {
            element.setSelectionEnabled(!immutable);
        }
    }

    @Override
    public void setDrawingBoardId(String boardId) {
        super.setDrawingBoardId(boardId);
        elementManager = drawingBoard.getElementManager();
        operationManager = drawingBoard.getOperationManager();
        drawingContext = drawingBoard.getDrawingContext();
    }

    @Override
    protected void onFirstPointDown(float x, float y) {
        element = new StrokeElement();
        element.setSelectionEnabled(!immutable);
        element.setPaint(assignPaint());
        elementManager.addElementToCurrentLayer(element);
        element.addPoint(x, y);
    }

    @Override
    protected void onOverPoint(float x, float y) {
        element.addPoint(x, y);
    }

    @Override
    protected void onLastPointUp(float x, float y, boolean singleTap) {
        element.addPoint(x, y);
        element.doneEditing();
        elementManager.removeElementFromCurrentLayer(element);
        operationManager.executeOperation(new InsertElementOperation(element));
    }

    @Override
    protected void onPointCancelled() {
        elementManager.removeElementFromCurrentLayer(element);
    }

    protected abstract CiPaint assignPaint();
}
