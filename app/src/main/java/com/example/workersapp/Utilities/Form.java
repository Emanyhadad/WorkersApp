package com.example.workersapp.Utilities;

import java.util.List;

public class Form {

    List<String> images;
    String description;
    List<String> categoriesList;
    String date;

    public Form() {
    }

    public Form(List<String> images, String description, List<String> categoriesList, String date) {
        this.images = images;
        this.description = description;
        this.categoriesList = categoriesList;
        this.date = date;
    }

    public Form(String description, List<String> categoriesList, String date) {
        this.description = description;
        this.categoriesList = categoriesList;
        this.date = date;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getCategoriesList() {
        return categoriesList;
    }

    public void setCategoriesList(List<String> categoriesList) {
        this.categoriesList = categoriesList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
