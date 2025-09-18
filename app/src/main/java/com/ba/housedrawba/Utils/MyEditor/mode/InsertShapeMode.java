package com.ba.housedrawba.Utils.MyEditor.mode;
import com.ba.housedrawba.Utils.MyEditor.core.CiPaint;
import com.ba.housedrawba.Utils.MyEditor.core.Vector2;
import com.ba.housedrawba.Utils.MyEditor.element.DrawElement;
import com.ba.housedrawba.Utils.MyEditor.element.shape.ShapeElement;
public class InsertShapeMode extends InsertVectorElementMode {
    private Class<? extends ShapeElement> shapeType;
    private ShapeElement shapeInstance;

    public InsertShapeMode() {}

    public void setShapeType(Class<? extends ShapeElement> shapeType) {
        this.shapeType = shapeType;
    }

    public void setShapeInstance(ShapeElement shapeInstance) {
        this.shapeInstance = shapeInstance;
    }

    @Override
    protected DrawElement createPreviewElement() {
        if (getShapeInstance()!=null) {
            previewElement = getShapeInstance();
            previewElement.setPaint(paintBuilder.createPreviewPaint(drawingContext.getPaint()));
        }
        return previewElement;
    }

    @Override
    protected DrawElement createRealElement(Vector2 vector) {
        if (getShapeInstance()!=null) {
            ShapeElement element = getShapeInstance();
            element.setPaint(new CiPaint(drawingContext.getPaint()));
            element.setupElementByVector(vector);
            return element;
        }else {
            return null;
        }
    }

    private ShapeElement getShapeInstance() {
        if (shapeInstance != null) {
            return (ShapeElement) shapeInstance.clone();
        } else if (shapeType != null) {
            try {
                return shapeType.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Cannot create shape.", e);
            }
        } else {
            throw new RuntimeException("Cannot find shape type or shape sample instance to create the new shape.");
        }
    }

}
