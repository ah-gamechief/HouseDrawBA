package com.ba.housedrawba.Utils.MyEditor.exception;

public class DrawingBoardNotFoundException extends RuntimeException {

    public DrawingBoardNotFoundException() {
        super();
    }

    public DrawingBoardNotFoundException(String detailMessage) {
        super(detailMessage);
    }

    public DrawingBoardNotFoundException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DrawingBoardNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
