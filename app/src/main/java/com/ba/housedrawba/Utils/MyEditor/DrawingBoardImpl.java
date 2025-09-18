package com.ba.housedrawba.Utils.MyEditor;
import com.ba.housedrawba.Utils.MyEditor.board.ElementManager;
import com.ba.housedrawba.Utils.MyEditor.board.ElementManagerImpl;
import com.ba.housedrawba.Utils.MyEditor.board.Layer;
import com.ba.housedrawba.Utils.MyEditor.operation.OperationManager;
import com.ba.housedrawba.Utils.MyEditor.operation.OperationManagerImpl;
import com.ba.housedrawba.Utils.MyEditor.persistence.ExportData;
import com.ba.housedrawba.Utils.MyEditor.persistence.PersistenceException;
import com.ba.housedrawba.Utils.MyEditor.persistence.PersistenceManager;
import com.ba.housedrawba.Utils.MyEditor.view.DrawingView;
import com.ba.housedrawba.Utils.MyEditor.view.DrawingViewProxy;
import com.ba.housedrawba.Utils.MyEditor.view.DrawingViewProxyImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class DrawingBoardImpl implements DrawingBoard {

    public static final String KEY_BOARD_ID = "boardId";
    private static final String KEY_LAYERS = "layers";

    private String boardId;
    private DrawingView drawingView;
    private final DrawingContext context;
    private final ElementManager elementManager;
    private final OperationManager operationManager;
    private final ConfigManager configManager;

    private PaintBuilder paintBuilder;
    private PaintingBehavior paintingBehavior;

    DrawingBoardImpl(String boardId) {
        this.boardId = boardId;
        context = new DrawingContext(boardId);
        context.addDrawingModeChangedListener(() -> {
            if (drawingView != null) {
                drawingView.notifyViewUpdated();
            }
        });
        elementManager = new ElementManagerImpl(boardId);
        operationManager = new OperationManagerImpl(boardId);
        configManager = new ConfigManagerImpl();
        paintBuilder = new DefaultPaintBuilder();
        paintingBehavior = new DefaultPaintingBehavior(paintBuilder);
    }

    @Override
    public String getBoardId() {
        return boardId;
    }

    @Override
    public void setupDrawingView(DrawingView view) {
        DrawingViewProxy viewProxy = new DrawingViewProxyImpl(view, context, elementManager);
        this.drawingView = view;
        this.drawingView.setViewProxy(viewProxy);
    }

    @Override
    public DrawingView getDrawingView() {
        return drawingView;
    }

    @Override
    public DrawingContext getDrawingContext() {
        return context;
    }

    @Override
    public ElementManager getElementManager() {
        return elementManager;
    }

    public OperationManager getOperationManager() {
        return operationManager;
    }

    @Override
    public ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public PaintBuilder getPaintBuilder() {
        return paintBuilder;
    }

    @Override
    public void setPaintBuilder(PaintBuilder paintBuilder) {
        this.paintBuilder = paintBuilder;
    }

    @Override
    public PaintingBehavior getPaintingBehavior() {
        return paintingBehavior;
    }

    @Override
    public void setPaintingBehavior(PaintingBehavior paintingBehavior) {
        this.paintingBehavior = paintingBehavior;
    }

    @Override
    public ExportData exportData() {
        JSONObject object = new JSONObject();
        try {
            object.put(KEY_BOARD_ID, boardId);
            Layer[] layers = elementManager.getLayers();
            object.put(KEY_LAYERS, PersistenceManager.persistObjects(Arrays.asList(layers)));
        } catch (JSONException e) {
            throw new PersistenceException(e);
        }

        Map<String, byte[]> resMap = new HashMap<>();
        for (Layer layer : elementManager.getLayers()) {
            Map<String, byte[]> map = layer.generateResources();
            if (map != null) {
                resMap.putAll(map);
            }
        }

        ExportData data = new ExportData();
        data.setMetaData(object);
        data.setResources(resMap);
        return data;
    }

    @Override
    public void importData(JSONObject object, Map<String, byte[]> resources) {
        if (object != null) {
            boardId = object.optString(KEY_BOARD_ID);
            try {
                List<Layer> layers = PersistenceManager.buildObjects(object.optJSONArray(KEY_LAYERS), resources);
                elementManager.removeAllLayers();
                for (Layer layer : layers) {
                    elementManager.addLayer(layer);
                }
            } catch (JSONException e) {
                throw new PersistenceException(e);
            }

            Layer[] layers = elementManager.getLayers();
            for (Layer layer : layers) {
                layer.afterLoaded();
            }
        }
    }

}
