package com.example.workersapp.Utilities;

import com.google.firebase.firestore.PropertyName;

public class Offer {
    public String offerBudget;
    public String offerDuration;
    public String offerDescription;
    public String workerID;

    public String countForm;

    public Offer(String offerBudget, String offerDuration, String offerDescription, String workerID) {
        this.offerBudget = offerBudget;
        this.offerDuration = offerDuration;
        this.offerDescription = offerDescription;
        this.workerID = workerID;
    }

    public String getCountForm( ) {
        return countForm;
    }

    public void setCountForm( String countForm ) {
        this.countForm = countForm;
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
}
