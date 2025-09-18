package com.ba.housedrawba.models;

public class SavedImagesModel {

    private String id;
    private String img;
    private boolean isChecked;

    public SavedImagesModel() {
    }

    public SavedImagesModel(String id, String img, boolean isChecked) {
        this.id = id;
        this.img = img;
        this.isChecked = isChecked;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
