package com.ba.housedrawba.Utils.MyEditor;
import com.ba.housedrawba.Utils.MyEditor.board.ElementManager;
import com.ba.housedrawba.Utils.MyEditor.operation.OperationManager;
import com.ba.housedrawba.Utils.MyEditor.persistence.ExportData;
import com.ba.housedrawba.Utils.MyEditor.view.DrawingView;

import org.json.JSONObject;

import java.util.Map;
public interface DrawingBoard {

    
    String getBoardId();

    
    void setupDrawingView(DrawingView view);

    
    DrawingView getDrawingView();


    DrawingContext getDrawingContext();


    ElementManager getElementManager();


    OperationManager getOperationManager();


    ConfigManager getConfigManager();


    PaintBuilder getPaintBuilder();


    void setPaintBuilder(PaintBuilder paintBuilder);


    PaintingBehavior getPaintingBehavior();


    void setPaintingBehavior(PaintingBehavior paintingBehavior);


    ExportData exportData();

    void importData(JSONObject metaData, Map<String, byte[]> resources);

}
