package com.ba.housedrawba.Utils.MyEditor;
import android.text.TextUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
public class DrawingBoardManager {

    private static DrawingBoardManager instance;

    private final Map<String, DrawingBoard> drawingBoardMap = new HashMap<>();


    public static DrawingBoardManager getInstance() {
        if (instance == null) {
            instance = new DrawingBoardManager();
        }
        return instance;
    }

    private DrawingBoardManager() {
    }


    public DrawingBoard createDrawingBoard() {
        DrawingBoard board = new DrawingBoardImpl(generateBoardId());
        drawingBoardMap.put(board.getBoardId(), board);
        return board;
    }


    public DrawingBoard createDrawingBoard(JSONObject object) {
        String boardId = extractBoardId(object);
        if (!TextUtils.isEmpty(boardId)) {
            DrawingBoard board = new DrawingBoardImpl(boardId);
            drawingBoardMap.put(board.getBoardId(), board);
            return board;
        } else {
            return null;
        }
    }


    public DrawingBoard findDrawingBoard(String boardId) {
        return drawingBoardMap.get(boardId);
    }

    private String generateBoardId() {
        return UUID.randomUUID().toString();
    }

    private String extractBoardId(JSONObject object) {
        return object.optString(DrawingBoardImpl.KEY_BOARD_ID);
    }

}
