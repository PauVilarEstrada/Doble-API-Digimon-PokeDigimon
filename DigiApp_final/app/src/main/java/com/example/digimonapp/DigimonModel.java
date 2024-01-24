package com.example.digimonapp;

public class DigimonModel {
    private String name;
    private String img;
    private String level;

    public DigimonModel(String name, String img, String level) {
        this.name = name;
        this.img = img;
        this.level = level;
    }

    public DigimonModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
