package com.ba.housedrawba.Utils.MyEditor;
import android.graphics.Paint;

import com.ba.housedrawba.Utils.MyEditor.core.CiPaint;
import com.ba.housedrawba.Utils.MyEditor.mode.DrawingMode;

import java.util.ArrayList;
import java.util.List;
public class DrawingContext {

    interface DrawingModeChangedListener {
        void onDrawingModeChanged();
    }

    private final String boardId;
    private DrawingMode drawingMode;
    private final List<DrawingModeChangedListener> drawingModeChangedListeners = new ArrayList<>();
    private CiPaint paint;

    public DrawingContext(String boardId) {
        this.boardId = boardId;
        setupDefault();
    }

    public DrawingMode getDrawingMode() {
        return drawingMode;
    }

    public void setDrawingMode(DrawingMode drawingMode) {
        if (drawingMode!=null) {
            if (this.drawingMode != null) this.drawingMode.onLeaveMode();
            this.drawingMode = drawingMode;
            this.drawingMode.setDrawingBoardId(boardId);
            this.drawingMode.onEnterMode();
        }
        // Notify drawing mode changed

        for (DrawingModeChangedListener listener : drawingModeChangedListeners) {
            listener.onDrawingModeChanged();
        }
    }

    public void addDrawingModeChangedListener(DrawingModeChangedListener listener) {
        drawingModeChangedListeners.add(listener);
    }

    public CiPaint getPaint() {
        if (paint == null) paint = new CiPaint();
        return paint;
    }

    private void setupDefault() {
        getPaint().setStyle(Paint.Style.STROKE);
    }

}
