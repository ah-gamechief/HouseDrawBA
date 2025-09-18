package com.ba.housedrawba.Utils.MyEditor.mode.eraser;
import com.ba.housedrawba.Utils.MyEditor.board.ElementManager;
import com.ba.housedrawba.Utils.MyEditor.element.DrawElement;
import com.ba.housedrawba.Utils.MyEditor.mode.BasePointMode;
import com.ba.housedrawba.Utils.MyEditor.operation.OperationManager;
import com.ba.housedrawba.Utils.MyEditor.operation.RemoveElementOperation;
public class ObjectEraserMode extends BasePointMode {

    private ElementManager elementManager;
    private OperationManager operationManager;

    @Override
    public void setDrawingBoardId(String boardId) {
        super.setDrawingBoardId(boardId);
        elementManager = drawingBoard.getElementManager();
        operationManager = drawingBoard.getOperationManager();
    }

    @Override
    protected void onOverPoint(float x, float y) {
        DrawElement element = elementManager.getFirstHitElement(x, y);
        if (element != null) {
            operationManager.executeOperation(new RemoveElementOperation(element));
        }
    }
}
