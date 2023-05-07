package com.example.workersapp.Utilities;

import java.util.List;

public class Model {

    List<String> images;
    String description;
    List<String> categoriesList;
    String date;

    String documentId;


    public Model() {
    }

    public Model(List<String> images, String documentId) {
        this.images = images;
        this.documentId = documentId;
    }

    public Model(List<String> images, String description, List<String> categoriesList, String date,String documentId) {
        this.images = images;
        this.description = description;
        this.categoriesList = categoriesList;
        this.date = date;
        this.documentId = documentId;
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

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
