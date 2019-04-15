package com.example.myapplication;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface DataCommunication {

    public void setMarkerList (HashMap<Integer, Marker> markerList);
    public HashMap<Integer, Marker> getMarkerList ();
    public void changeCameraListener(double lat, double lng);
    public void setLocationSpotList(List<LocationSpotModel> locationSpotList);
    public List<LocationSpotModel> getlocationSpotList();
    public LocationSpotModel getlocationSpot(Integer arrayIndex);


}
