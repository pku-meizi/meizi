package com.meiziaccess.back;

/**
 * Created by sun on 2016/11/27.
 */
public class Addresses {
    private String highCodeVideoPath;
    private String lowCodeVideoPath;
    private String keyFramePath;
    private String catalgedXmlPath;
    private String unCatalgedXmlPath;
    private String name;

    public Addresses() {
    }

    public Addresses(String highCodeVideoPath, String lowCodeVideoPath, String keyFramePath, String catalgedXmlPath, String unCatalgedXmlPath, String name) {
        this.highCodeVideoPath = highCodeVideoPath;
        this.lowCodeVideoPath = lowCodeVideoPath;
        this.keyFramePath = keyFramePath;
        this.catalgedXmlPath = catalgedXmlPath;
        this.unCatalgedXmlPath = unCatalgedXmlPath;
        this.name = name;
    }

    public String getHighCodeVideoPath() {
        return highCodeVideoPath;
    }

    public void setHighCodeVideoPath(String highCodeVideoPath) {
        this.highCodeVideoPath = highCodeVideoPath;
    }

    public String getLowCodeVideoPath() {
        return lowCodeVideoPath;
    }

    public void setLowCodeVideoPath(String lowCodeVideoPath) {
        this.lowCodeVideoPath = lowCodeVideoPath;
    }

    public String getKeyFramePath() {
        return keyFramePath;
    }

    public void setKeyFramePath(String keyFramePath) {
        this.keyFramePath = keyFramePath;
    }

    public String getCatalgedXmlPath() {
        return catalgedXmlPath;
    }

    public void setCatalgedXmlPath(String catalgedXmlPath) {
        this.catalgedXmlPath = catalgedXmlPath;
    }

    public String getUnCatalgedXmlPath() {
        return unCatalgedXmlPath;
    }

    public void setUnCatalgedXmlPath(String unCatalgedXmlPath) {
        this.unCatalgedXmlPath = unCatalgedXmlPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
