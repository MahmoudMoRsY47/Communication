package com.example.communication;

public class User {
    private String username,phone,profileUrl;


    public User(){}
    public User(String username,String phone,String profileUrl)
    {
        this.username=username;
        this.phone=phone;
        this.profileUrl=profileUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return phone;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
