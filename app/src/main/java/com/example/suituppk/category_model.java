package com.example.suituppk;

public class category_model {

    private String categoryiconlink;
    private String categoryName;

    public category_model(String categoryiconlink, String categoryName) {
        this.categoryiconlink = categoryiconlink;
        this.categoryName = categoryName;
    }

    public String getCategoryiconlink() {
        return categoryiconlink;
    }

    public void setCategoryiconlink(String categoryiconlink) {
        this.categoryiconlink = categoryiconlink;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
