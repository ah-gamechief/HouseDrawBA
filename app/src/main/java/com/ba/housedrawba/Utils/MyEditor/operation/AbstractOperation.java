package com.ba.housedrawba.Utils.MyEditor.operation;

import com.ba.housedrawba.Utils.MyEditor.DrawingBoard;
import com.ba.housedrawba.Utils.MyEditor.DrawingBoardManager;

public abstract class AbstractOperation implements DrawingOperation {

    protected DrawingBoard drawingBoard;

    @Override
    public void setDrawingBoardId(String boardId) {
        this.drawingBoard = DrawingBoardManager.getInstance().findDrawingBoard(boardId);
    }

    @Override
    public void redo() {
        doOperation();
    }
}
