package com.example.myapplication;

import com.google.firebase.firestore.GeoPoint;

public class LocationSpotModel {


    public LocationSpotModel(String locationId, String locationName, GeoPoint latlng, int maxCompacity, int currentCompacity, String locationAddress, String openCloseTime) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.latlng = latlng;
        this.maxCompacity = maxCompacity;
        this.currentCompacity = currentCompacity;
        this.locationAddress = locationAddress;
        this.openCloseTime = openCloseTime;

    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
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

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getOpenCloseTime() {
        return openCloseTime;
    }

    public void setOpenCloseTime(String openCloseTime) {
        this.openCloseTime = openCloseTime;
    }

    private String locationId;
    private String locationName;
    private GeoPoint latlng;

    private int maxCompacity;

    private int currentCompacity;

    private String locationAddress;
    private String openCloseTime;


    public String getGarageImg() {
        return garageImg;
    }

    public void setGarageImg(String garageImg) {
        this.garageImg = garageImg;
    }

    private String garageImg;


    private LocationSpotModel(){}

    public GeoPoint getLatlng() {
        return latlng;
    }

    public void setLatlng(GeoPoint latlng) {
        this.latlng = latlng;
    }


    //Capacity graph?
    //Average traffic?
    //List of reviews?? requires users information and comment info.
    //image





}
