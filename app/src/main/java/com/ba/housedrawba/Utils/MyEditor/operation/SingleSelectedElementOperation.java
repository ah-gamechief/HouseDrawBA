package com.ba.housedrawba.Utils.MyEditor.operation;
import com.ba.housedrawba.Utils.MyEditor.board.ElementManager;
import com.ba.housedrawba.Utils.MyEditor.element.DrawElement;
public abstract class SingleSelectedElementOperation extends AbstractOperation {

    protected ElementManager elementManager;
    protected DrawElement selectedElement;

    @Override
    public void setDrawingBoardId(String boardId) {
        super.setDrawingBoardId(boardId);
        elementManager = drawingBoard.getElementManager();
    }

    @Override
    public boolean isExecutable() {
        if (selectedElement == null) {
            selectedElement = elementManager.getSelection().getSingleElement();
        }
        return selectedElement != null;
    }

}