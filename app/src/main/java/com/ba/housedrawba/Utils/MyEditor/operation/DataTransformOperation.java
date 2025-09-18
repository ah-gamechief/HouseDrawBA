package com.ba.housedrawba.Utils.MyEditor.operation;

import android.graphics.Matrix;

import com.ba.housedrawba.Utils.MyEditor.element.DrawElement;
import com.ba.housedrawba.Utils.MyEditor.utils.MatrixUtils;
public class DataTransformOperation extends AbstractOperation {

    private final DrawElement element;
    private final Matrix deltaMatrix;

    public DataTransformOperation(DrawElement element, Matrix deltaMatrix) {
        this.element = element;
        this.deltaMatrix = deltaMatrix;
    }

    @Override
    public boolean isExecutable() {
        return element != null;
    }

    @Override
    public boolean doOperation() {
        element.applyMatrixForData(deltaMatrix);
        element.updateBoundingBox();
        drawingBoard.getDrawingView().notifyViewUpdated();
        return true;
    }

    @Override
    public void undo() {
        element.applyMatrixForData(MatrixUtils.getInvertMatrix(deltaMatrix));
        element.updateBoundingBox();
        drawingBoard.getDrawingView().notifyViewUpdated();
    }

}
