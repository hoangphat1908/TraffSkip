package com.example.myapplication;

import java.util.ArrayList;

public class RecyclerViewFragmentAbstractModel {

    private String title;

    private String message;


    public RecyclerViewFragmentAbstractModel(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public RecyclerViewFragmentAbstractModel() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
