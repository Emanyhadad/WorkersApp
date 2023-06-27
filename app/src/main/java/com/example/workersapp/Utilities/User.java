package com.example.workersapp.Utilities;

import java.util.Map;

public class User{
    private String fullName;
    private String nickName;
    private String birth;
    private String gender;
    private String image;
    private String title;
    private String city;
    private String cv;
    private String work;

    private String accountType;

    public User(String fullName, String nickName, String birth, String gender) {
        this.fullName = fullName;
        this.nickName = nickName;
        this.birth = birth;
        this.gender = gender;
    }

    public User(String fullName, String nickName, String birth, String gender, String image, String accountType) {
        this.fullName = fullName;
        this.nickName = nickName;
        this.birth = birth;
        this.gender = gender;
        this.image = image;
        this.accountType = accountType;
    }

    public User(String fullName, String nickName, String birth, String gender, String image) {
        this.fullName = fullName;
        this.nickName = nickName;
        this.birth = birth;
        this.gender = gender;
        this.image = image;
    }

    public User(String title, String city) {
        this.title = title;
        this.city = city;
    }


    public User() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
