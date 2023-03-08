package com.example.suituppk;

import java.util.List;

public class HomePageModel {

    public static final int BANNER_SLIDER = 0;
    public static final int STRIP_AD_BANNER = 1;
    public static final int HORIZONTAL_PRODUCT_VIEW = 2;
    public static final int GRID_PRODUCT_VIEW = 3;

    private int type;
    private String backgroundcolour;

    ///////////////// Banner Slider
    private List<sliderModel> sliderModelList;

    public HomePageModel(int type, List<sliderModel> sliderModelList) {
        this.type = type;
        this.sliderModelList = sliderModelList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<sliderModel> getSliderModelList() {
        return sliderModelList;
    }

    public void setSliderModelList(List<sliderModel> sliderModelList) {
        this.sliderModelList = sliderModelList;
    }

    ////////////////////  Banner slider

    ///////////////////   Strip Ad

    private String resource;


    public HomePageModel(int type, String resource, String backgroundcolour) {
        this.type = type;
        this.resource = resource;
        this.backgroundcolour = backgroundcolour;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getBackgroundcolour() {
        return backgroundcolour;
    }

    public void setBackgroundcolour(String backgroundcolour) {
        this.backgroundcolour = backgroundcolour;
    }
    ///////////////////   Strip Ad




    private String title;
    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;

    //////////////////   HORIZONTAL PRODUCT LAYOUT

    private List<WishlistModel> viewAllProductList;

    public HomePageModel(int type, String title, String backgroundcolour ,List<HorizontalProductScrollModel> horizontalProductScrollModelList , List<WishlistModel> viewAllProductList) {
        this.type = type;
        this.title = title;
        this.backgroundcolour = backgroundcolour;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
        this.viewAllProductList = viewAllProductList;
    }

    public List<WishlistModel> getViewAllProductList() {
        return viewAllProductList;
    }

    public void setViewAllProductList(List<WishlistModel> viewAllProductList) {
        this.viewAllProductList = viewAllProductList;
    }


    //////////////////   HORIZONTAL PRODUCT LAYOUT


    /////////////////grid product layout

    public HomePageModel(int type, String title, String backgroundcolour , List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.type = type;
        this.title = title;
        this.backgroundcolour = backgroundcolour;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }


    /////////////////////grid product layout

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<HorizontalProductScrollModel> getHorizontalProductScrollModelList() {
        return horizontalProductScrollModelList;
    }

    public void setHorizontalProductScrollModelList(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    //////////////////   HORIZONTAL PRODUCT LAYOUT

}
