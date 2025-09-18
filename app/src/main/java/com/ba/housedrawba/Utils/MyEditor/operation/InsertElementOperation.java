package com.ba.housedrawba.Utils.MyEditor.operation;
import com.ba.housedrawba.Utils.MyEditor.board.ElementManager;
import com.ba.housedrawba.Utils.MyEditor.element.DrawElement;
public class InsertElementOperation extends AbstractOperation {

    private ElementManager elementManager;
    private final DrawElement element;

    public InsertElementOperation(DrawElement element) {
        this.element = element;
    }

    @Override
    public void setDrawingBoardId(String boardId) {
        super.setDrawingBoardId(boardId);
        elementManager = drawingBoard.getElementManager();
    }

    @Override
    public boolean isExecutable() {
        return element != null;
    }

    @Override
    public boolean doOperation() {
        elementManager.addElementToCurrentLayer(element);
        drawingBoard.getDrawingView().notifyViewUpdated();
        return true;
    }

    @Override
    public void undo() {
        elementManager.removeElementFromCurrentLayer(element);
        drawingBoard.getDrawingView().notifyViewUpdated();
    }
}
