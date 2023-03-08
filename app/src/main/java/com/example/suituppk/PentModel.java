package com.example.suituppk;

public class PentModel {
    private String product_image_1 , product_image_2, product_image_3;

    public PentModel() {

    }

    public PentModel(String product_image_1, String product_image_2, String product_image_3) {
        this.product_image_1 = product_image_1;
        this.product_image_2 = product_image_2;
        this.product_image_3 = product_image_3;
    }

    public String getProduct_image_1() {
        return product_image_1;
    }

    public void setProduct_image_1(String product_image_1) {
        this.product_image_1 = product_image_1;
    }

    public String getProduct_image_2() {
        return product_image_2;
    }

    public void setProduct_image_2(String product_image_2) {
        this.product_image_2 = product_image_2;
    }

    public String getProduct_image_3() {
        return product_image_3;
    }

    public void setProduct_image_3(String product_image_3) {
        this.product_image_3 = product_image_3;
    }
}
