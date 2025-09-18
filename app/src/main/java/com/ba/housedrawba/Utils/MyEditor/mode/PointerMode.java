package com.ba.housedrawba.Utils.MyEditor.mode;
import android.util.Log;
import android.view.MotionEvent;

import com.ba.housedrawba.Utils.MyEditor.CircleLog;
import com.ba.housedrawba.Utils.MyEditor.OnDrawItemSelectedListener;
import com.ba.housedrawba.Utils.MyEditor.board.ElementManager;
import com.ba.housedrawba.Utils.MyEditor.element.DrawElement;
import com.ba.housedrawba.Utils.MyEditor.element.behavior.ResizingDirection;
import com.ba.housedrawba.Utils.MyEditor.mode.selection.RectSelectionMode;
import com.ba.housedrawba.Utils.MyEditor.mode.selection.SelectionMode;
import com.ba.housedrawba.Utils.MyEditor.mode.transformation.MoveMode;
import com.ba.housedrawba.Utils.MyEditor.mode.transformation.MoveReferencePointMode;
import com.ba.housedrawba.Utils.MyEditor.mode.transformation.ResizeMode;
import com.ba.housedrawba.Utils.MyEditor.mode.transformation.RotateMode;
public class PointerMode extends CompositeMode {
    private static final String TAG = "PointerMode";
    private OnDrawItemSelectedListener onDrawItemSelectedListener;

    private ElementManager elementManager;
    private DrawingMode currentMode;
    private SelectionMode selectionMode = new RectSelectionMode();
    private final MoveReferencePointMode moveReferencePointMode = new MoveReferencePointMode();
    private final MoveMode moveMode = new MoveMode();
    private final RotateMode rotateMode = new RotateMode();
    private final ResizeMode resizeMode = new ResizeMode();

    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }
    public void setClickCallbackInterface(OnDrawItemSelectedListener onDrawItemSelectedListener) {
        this.onDrawItemSelectedListener = onDrawItemSelectedListener;
    }

    @Override
    public void setDrawingBoardId(String boardId) {
        super.setDrawingBoardId(boardId);
        elementManager = drawingBoard.getElementManager();
        if (selectionMode!=null) selectionMode.setDrawingBoardId(boardId);
        moveReferencePointMode.setDrawingBoardId(boardId);
        moveMode.setDrawingBoardId(boardId);
        rotateMode.setDrawingBoardId(boardId);
        resizeMode.setDrawingBoardId(boardId);
    }

    @Override
    public void onLeaveMode() {
        if (currentMode != null) currentMode.onLeaveMode();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Auto switch mode
                hitTestForSwitchingMode(event.getX(), event.getY());
                if (currentMode!=null) currentMode.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentMode!=null) currentMode.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_UP:
                if (currentMode!=null) currentMode.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                if (currentMode!=null) currentMode.onTouchEvent(event);
                break;
        }
        return true;
    }

    private void hitTestForSwitchingMode(float x, float y) {
        if (elementManager!=null && elementManager.getCurrentObjects()!=null) {
            for (int i = elementManager.getCurrentObjects().length - 1; i >= 0; i--) {
                DrawElement element = elementManager.getCurrentObjects()[i];
                if (element!=null) {
                    if (element.isSelected()) {
                        if (element.hitTestForReferencePoint(x, y)) {
                            moveReferencePointMode.setElement(element);
                            currentMode = moveReferencePointMode;
                            CircleLog.i(TAG, "Switch to ReferencePointMode");
                            return;
                        }

                        if (element.hitTestForRotationHandle(x, y)) {
                            rotateMode.setElement(element);
                            currentMode = rotateMode;

                            CircleLog.i(TAG, "Switch to RotateMode");
                            return;
                        }

                        ResizingDirection direction = element.hitTestForResizingHandle(x, y);
                        if (direction != ResizingDirection.NONE) {
                            resizeMode.setElement(element);
                            resizeMode.setResizingDirection(direction);
                            currentMode = resizeMode;
                            CircleLog.i(TAG, "Switch to ResizeMode");
                            return;
                        }

                        if (element.hitTestForSelection(x, y)) {
                            moveMode.setElement(element);
                            currentMode = moveMode;
                            CircleLog.i(TAG, "Switch to MoveMode");
                            return;
                        }

                        if (onDrawItemSelectedListener != null)
                            onDrawItemSelectedListener.stickerItemSelected(element.isSelected());

                    } else {
                        if (onDrawItemSelectedListener != null)
                            onDrawItemSelectedListener.stickerItemSelected(false);
                    }
                    Log.e("PhotoElement_05", "Its Clicked " + element.getName() + "=" + element.isSelected());
                }
            }
            currentMode = selectionMode;
            CircleLog.i(TAG, "Switch to SelectionMode");
        }
    }

}
