package com.ba.housedrawba.Utils.MyEditor.operation;
import com.ba.housedrawba.Utils.MyEditor.board.ElementManager;
import com.ba.housedrawba.Utils.MyEditor.element.DrawElement;
public class RemoveElementOperation extends AbstractOperation {

    private ElementManager elementManager;
    private final DrawElement element;

    public RemoveElementOperation(DrawElement element) {
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
        elementManager.removeElementFromCurrentLayer(element);
        drawingBoard.getDrawingView().notifyViewUpdated();
        return true;
    }

    @Override
    public void undo() {
        elementManager.addElementToCurrentLayer(element);
        drawingBoard.getDrawingView().notifyViewUpdated();
    }

}
