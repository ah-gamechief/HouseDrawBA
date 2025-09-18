package com.ba.housedrawba.Utils.MyEditor.operation;

public interface DrawingOperation {

    void setDrawingBoardId(String boardId);

    boolean isExecutable();

    boolean doOperation();

    void undo();

    void redo();

}
