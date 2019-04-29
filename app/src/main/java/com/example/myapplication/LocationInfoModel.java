package com.example.myapplication;

import com.amazonaws.amplify.generated.graphql.GetLocationInfoQuery;
import com.amazonaws.amplify.generated.graphql.ListLocationInfosQuery;

import java.util.List;

public class LocationInfoModel {
    private int totalSpots;
    private int remainingSpots;
    private List<String> imageList;
    public LocationInfoModel(GetLocationInfoQuery.GetLocationInfo locationInfo) {
        this.totalSpots = locationInfo.total();
        this.remainingSpots = locationInfo.remaining();
    }


    public int getTotalSpots() {
        return totalSpots;
    }

    public void setTotalSpots(int totalSpots) {
        this.totalSpots = totalSpots;
    }

    public int getRemainingSpots() {
        return remainingSpots;
    }

    public void setRemainingSpots(int remainingSpots) {
        this.remainingSpots = remainingSpots;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }
}
