package com.example.myapplication;

import java.util.ArrayList;

public class RecyclerViewFragmentAbstractModel {

    private int itemId;
    private String itemName;
    private double lat;
    private double lng;
    private int maxCompacity;

    private int currentCompacity;

    public RecyclerViewFragmentAbstractModel(int id, String title, double latitude, double longitude,  int cCompacity, int mCompacity) {
        this.itemId = id;
        this.itemName = title;
        this.lat = latitude;
        this.lng = longitude;
        this.maxCompacity = mCompacity;
        this.currentCompacity = cCompacity;
    }

    public RecyclerViewFragmentAbstractModel() {

    }
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getMaxCompacity() {
        return maxCompacity;
    }

    public void setMaxCompacity(int maxCompacity) {
        this.maxCompacity = maxCompacity;
    }

    public int getCurrentCompacity() {
        return currentCompacity;
    }

    public void setCurrentCompacity(int currentCompacity) {
        this.currentCompacity = currentCompacity;
    }


}
