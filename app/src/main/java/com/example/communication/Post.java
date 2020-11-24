package com.example.communication;

import java.util.List;

public class Post {
    private String postid, username, userprofile, postcontent, userid;
    private List<Likes> likes;

    public Post() {
    }

    public Post(String postid, String username, String userprofile, String postcontent, String userid, List<Likes> likes) {
        this.postid = postid;
        this.username = username;
        this.userprofile = userprofile;
        this.postcontent = postcontent;
        this.userid = userid;
        this.likes = likes;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserprofile() {
        return userprofile;
    }

    public void setUserprofile(String userprofile) {
        this.userprofile = userprofile;
    }

    public String getPostcontent() {
        return postcontent;
    }

    public void setPostcontent(String postcontent) {
        this.postcontent = postcontent;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public List<Likes> getLikes() {
        return likes;
    }

    public void setLikes(List<Likes> likes) {
        this.likes = likes;
    }
}