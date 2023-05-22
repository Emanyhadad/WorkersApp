package com.example.workersapp.Utilities;

public class WorkerReviews {
    String jobId;
    String clintId;
    String jobTitle;
    String jobFinishDate;
    String Rating_worker;
    String Comment_worker;

    public WorkerReviews( ) {
    }

    public WorkerReviews( String jobId , String clintId , String jobTitle , String jobFinishDate , String rating_worker , String comment_worker ) {
        this.jobId = jobId;
        this.clintId = clintId;
        this.jobTitle = jobTitle;
        this.jobFinishDate = jobFinishDate;
        Rating_worker = rating_worker;
        Comment_worker = comment_worker;
    }

    public String getJobId( ) {
        return jobId;
    }

    public void setJobId( String jobId ) {
        this.jobId = jobId;
    }

    public String getClintId( ) {
        return clintId;
    }

    public void setClintId( String clintId ) {
        this.clintId = clintId;
    }

    public String getJobTitle( ) {
        return jobTitle;
    }

    public void setJobTitle( String jobTitle ) {
        this.jobTitle = jobTitle;
    }

    public String getJobFinishDate( ) {
        return jobFinishDate;
    }

    public void setJobFinishDate( String jobFinishDate ) {
        this.jobFinishDate = jobFinishDate;
    }

    public String getRating_worker( ) {
        return Rating_worker;
    }

    public void setRating_worker( String rating_worker ) {
        Rating_worker = rating_worker;
    }

    public String getComment_worker( ) {
        return Comment_worker;
    }

    public void setComment_worker( String comment_worker ) {
        Comment_worker = comment_worker;
    }

    @Override
    public String toString( ) {
        return "WorkerReviews{" +
                "jobId='" + jobId + '\'' +
                ", clintId='" + clintId + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", jobFinishDate='" + jobFinishDate + '\'' +
                ", Rating_worker='" + Rating_worker + '\'' +
                ", Comment_worker='" + Comment_worker + '\'' +
                '}';
    }
}
