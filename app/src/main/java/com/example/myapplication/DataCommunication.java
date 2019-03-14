package com.example.myapplication;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;

public interface DataCommunication {

    public ArrayList<RecyclerViewFragmentAbstractModel> getModelList();
    public void setModelList(ArrayList<RecyclerViewFragmentAbstractModel> modelList);
    public void setMarkerList (HashMap<Integer, Marker> markerList);
    public HashMap<Integer, Marker> getMarkerList ();
    public void changeCameraListener(double lat, double lng);


}