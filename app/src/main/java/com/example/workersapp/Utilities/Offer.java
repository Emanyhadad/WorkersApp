package com.example.workersapp.Utilities;

import com.google.firebase.firestore.PropertyName;

public class Offer {
    public String offerBudget;
    public String offerDuration;
    public String offerDescription;
    public String workerID;
    public String clintID;
    public String postID;

    public Offer( String offerBudget , String offerDuration , String offerDescription , String workerID , String clintID , String postID ) {
        this.offerBudget = offerBudget;
        this.offerDuration = offerDuration;
        this.offerDescription = offerDescription;
        this.workerID = workerID;
        this.clintID = clintID;
        this.postID = postID;
    }

    public String getOfferBudget( ) {
        return offerBudget;
    }

    public void setOfferBudget( String offerBudget ) {
        this.offerBudget = offerBudget;
    }

    public String getOfferDuration( ) {
        return offerDuration;
    }

    public void setOfferDuration( String offerDuration ) {
        this.offerDuration = offerDuration;
    }

    public String getOfferDescription( ) {
        return offerDescription;
    }

    public void setOfferDescription( String offerDescription ) {
        this.offerDescription = offerDescription;
    }

    public String getWorkerID( ) {
        return workerID;
    }

    public void setWorkerID( String workerID ) {
        this.workerID = workerID;
    }

    public String getClintID( ) {
        return clintID;
    }

    public void setClintID( String clintID ) {
        this.clintID = clintID;
    }

    public String getPostID( ) {
        return postID;
    }

    public void setPostID( String postID ) {
        this.postID = postID;
    }
}
