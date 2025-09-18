package com.ba.housedrawba.Utils.MyEditor.mode;
import com.ba.housedrawba.Utils.MyEditor.DrawingBoard;
import com.ba.housedrawba.Utils.MyEditor.DrawingBoardManager;
public abstract class AbstractDrawingMode implements DrawingMode {
    protected DrawingBoard drawingBoard;

    public AbstractDrawingMode() {}

    @Override
    public void setDrawingBoardId(String boardId) {
        drawingBoard = DrawingBoardManager.getInstance().findDrawingBoard(boardId);
    }

    @Override
    public void onEnterMode() {
    }

    @Override
    public void onLeaveMode() {
    }

}
