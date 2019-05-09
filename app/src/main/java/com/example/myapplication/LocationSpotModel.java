package com.example.myapplication;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class LocationSpotModel {
    private String locationId;
    private String locationName;
    private GeoPoint latlng;
    private String locationAddress;
    private String openCloseTime;
    private LocationInfoModel locationInfo;
    private String garageImg;

    private String parkingRate;

    private List<Floor> floors;

    private LocationSpotModel(){}

    public LocationSpotModel(String locationId, String locationName, GeoPoint latlng, String locationAddress, String openCloseTime) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.latlng = latlng;
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

    public String getGarageImg() {
        return garageImg;
    }

    public void setGarageImg(String garageImg) {
        this.garageImg = garageImg;
    }

    public GeoPoint getLatlng() {
        return latlng;
    }

    public void setLatlng(GeoPoint latlng) {
        this.latlng = latlng;
    }

    public LocationInfoModel getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(LocationInfoModel locationInfo) {
        this.locationInfo = locationInfo;
    }
    public String getParkingRate() {
        return parkingRate;
    }

    public void setParkingRate(String parkingRate) {
        this.parkingRate = parkingRate;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public void setFloors(List<Floor> floors) {
        this.floors = floors;
    }
}
