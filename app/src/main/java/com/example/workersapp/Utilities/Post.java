package com.example.workersapp.Utilities;

import java.util.List;

public class Post {
    String title;
    String description;
    List<String> images;
    List<String> categoriesList;
    String expectedWorkDuration;
    String projectedBudget;
    String jobLocation;
    String jobState;
    String workerId;
    String postId;
    String OwnerId;
    public Post(String title, String description, List<String> images, List<String> categoriesList,
                String expectedWorkDuration, String projectedBudget, String jobLocation, String jobState) {
        this.title = title;
        this.description = description;
        this.images = images;
        this.categoriesList = categoriesList;
        this.expectedWorkDuration = expectedWorkDuration;
        this.projectedBudget = projectedBudget;
        this.jobLocation = jobLocation;
        this.jobState = jobState;
    }

    public String getTitle() {
        return title;
    }

    public String getPostId( ) {
        return postId;
    }

    public void setPostId( String postId ) {
        this.postId = postId;
    }

    public void setTitle( String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getCategoriesList() {
        return categoriesList;
    }

    public void setCategoriesList(List<String> categoriesList) {
        this.categoriesList = categoriesList;
    }

    public String getExpectedWorkDuration() {
        return expectedWorkDuration;
    }

    public void setExpectedWorkDuration(String expectedWorkDuration) {
        this.expectedWorkDuration = expectedWorkDuration;
    }

    public String getProjectedBudget() {
        return projectedBudget;
    }

    public void setProjectedBudget(String projectedBudget) {
        this.projectedBudget = projectedBudget;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public String getJobState() {
        return jobState;
    }

    public void setJobState(String jobState) {
        this.jobState = jobState;
    }

    public String getWorkerId( ) {
        return workerId;
    }

    public void setWorkerId( String workerId ) {
        this.workerId = workerId;
    }

    public String getOwnerId( ) {
        return OwnerId;
    }

    public void setOwnerId( String ownerId ) {
        OwnerId = ownerId;
    }

    @Override
    public String toString( ) {
        return "Post{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", images=" + images +
                ", categoriesList=" + categoriesList +
                ", expectedWorkDuration='" + expectedWorkDuration + '\'' +
                ", projectedBudget='" + projectedBudget + '\'' +
                ", jobLocation='" + jobLocation + '\'' +
                ", jobState='" + jobState + '\'' +
                ", workerId='" + workerId + '\'' +
                ", postId='" + postId + '\'' +
                ", OwnerId='" + OwnerId + '\'' +
                '}';
    }
}
