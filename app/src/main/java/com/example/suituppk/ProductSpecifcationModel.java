package com.example.suituppk;

public class ProductSpecifcationModel {


    public static final int SPECIFICATION_TITLE = 0;
    public static final int SPECIFICATION_BODY = 1;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    ////////////// Specification Title
    private String title;

    public ProductSpecifcationModel(int type, String title) {
        this.type = type;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    ////////////// Specification Title


    ////////////// Specification Body
    private String featureName;
    private String FeatureValue;

    public ProductSpecifcationModel(int type, String featureName, String featureValue) {
        this.type = type;
        this.featureName = featureName;
        FeatureValue = featureValue;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureValue() {
        return FeatureValue;
    }

    public void setFeatureValue(String featureValue) {
        FeatureValue = featureValue;
    }
    ////////////// Specification Body

}
