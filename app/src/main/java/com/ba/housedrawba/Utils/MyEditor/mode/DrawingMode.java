package com.ba.housedrawba.Utils.MyEditor.mode;

import android.view.MotionEvent;

public interface DrawingMode {


    void setDrawingBoardId(String boardId);

    void  onEnterMode();

    void onLeaveMode();

    boolean onTouchEvent(MotionEvent event);

}
