package com.example.myapplication;

public class Comment {
    private String username;
    private String body;
    public Comment() {}
    public Comment(String username, String body) {
        this.username = username;
        this.body = body;
    }

    public String getUsername() {
        return username;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
