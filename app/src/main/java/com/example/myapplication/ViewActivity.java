package com.example.myapplication;

import android.content.Intent;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


// Using min SDK 16 or higher

public class ViewActivity extends AppCompatActivity implements DataCommunication, BottomSheetFrag.BottomSheetListener {

    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    List<LocationSpotModel> locationSpotList;

    ////
    private FirebaseAuth mAuth;
    private static final String TAG = "ViewActivity";
    private ArrayList<RecyclerViewFragmentAbstractModel> modelList;
    private HashMap<Integer, Marker> markerList;

    private BottomSheetBehavior mBottomSheetBehavior;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        mAuth = FirebaseAuth.getInstance();
        authenticate();

        locationSpotList = new ArrayList<>();



    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    public void authenticate() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        TextView greetingsTV = (TextView) findViewById(R.id.tv_greetings);
        Button authButton = (Button) findViewById(R.id.btn_to_auth);
        Button signOutButton = (Button) findViewById(R.id.btn_sign_out);

        if (currentUser != null) {
            greetingsTV.setVisibility(View.VISIBLE);
            greetingsTV.setText("Hello, " + currentUser.getEmail());
            authButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
        }
        else {
            greetingsTV.setVisibility(View.GONE);
            authButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
        }
    }

    public void switchToAuth(View view) {
        startActivity(new Intent(this, AuthorizationActivity.class));
    }



    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        authenticate();
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

    @Override
   public void setLocationSpotList(List<LocationSpotModel> locationSpotList) {
        this.locationSpotList = locationSpotList;

    }

    @Override
    public List<LocationSpotModel> getlocationSpotList() {
        Log.d(TAG, "createLocationSpotList:  called, getlocationSpotList:  size: "+locationSpotList.size());
        return locationSpotList;
    }

    @Override
    public LocationSpotModel getlocationSpot(Integer arrayIndex) {
        LocationSpotModel data  = locationSpotList.get(arrayIndex);
        return data;
    }


    @Override
    public void onDirectionClicked(Marker marker) {
        MapsActivity fragmentB = (MapsActivity)getSupportFragmentManager().findFragmentById(R.id.maps_activity);
        fragmentB.calculateDirections(marker);

    }
}

