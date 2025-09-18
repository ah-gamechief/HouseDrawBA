package com.ba.housedrawba.Utils.MyEditor.operation;

import android.graphics.Matrix;

public class ReshapeOperation extends SingleSelectedElementOperation {

    private Matrix displayMatrix;

    @Override
    public boolean doOperation() {
        displayMatrix = selectedElement.applyDisplayMatrixToData();
        selectedElement.resetReferencePoint();
        drawingBoard.getDrawingView().notifyViewUpdated();
        return true;
    }

    @Override
    public void undo() {
        if (selectedElement != null) {
            selectedElement.restoreDisplayMatrixFromData(displayMatrix);
            drawingBoard.getDrawingView().notifyViewUpdated();
        }
    }

}
