package com.example.workersapp.Utilities;

import com.google.firebase.firestore.PropertyName;

public class Offer {
    @PropertyName("offerBudget")
    public String offerBudget;

    @PropertyName("offerDuration")
    public String offerDuration;

    @PropertyName("offerDescription")
    public String offerDescription;

    @PropertyName("WorkerID")
    public String workerID;

    @PropertyName("WorkerFormsCount")
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
}
