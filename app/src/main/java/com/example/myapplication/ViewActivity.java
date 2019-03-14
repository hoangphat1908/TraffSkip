package com.example.myapplication;

import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;

//AppCompatActivity
public class ViewActivity extends AppCompatActivity implements DataCommunication {
    private static final String TAG = "ViewActivity";
    private ArrayList<RecyclerViewFragmentAbstractModel> modelList;
    private HashMap<Integer, Marker> markerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

    }


    @Override
    public ArrayList<RecyclerViewFragmentAbstractModel> getModelList(){
        return modelList;
    }

    @Override
    public void setModelList(ArrayList<RecyclerViewFragmentAbstractModel> modelList) {

        this.modelList = modelList;
    }

    @Override
    public void setMarkerList(HashMap<Integer, Marker> markerList) {
        this.markerList = markerList;
    }

    @Override
    public HashMap<Integer, Marker> getMarkerList() {
        return markerList;
    }

    @Override
    public void changeCameraListener(double lat, double lng) {
        MapsActivity fragmentB = (MapsActivity)getSupportFragmentManager().findFragmentById(R.id.maps_activity);
        fragmentB.mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(
                new LatLng(lat,lng)),
                600,null
        );
    }


}

