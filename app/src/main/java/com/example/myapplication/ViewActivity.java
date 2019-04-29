package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.amplify.generated.graphql.ListLocationInfosQuery;
import com.amazonaws.amplify.generated.graphql.ListTodosQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nonnull;

import static android.support.constraint.Constraints.TAG;
import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;


// Using min SDK 16 or higher

public class ViewActivity extends AppCompatActivity implements DataCommunication, BottomSheetFrag.BottomSheetListener {

    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    List<LocationSpotModel> locationSpotList;

    ////
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final String TAG = "ViewActivity";
    private HashMap<Integer, Marker> markerList;
    private Circle searchCircle;
    private Circle areaCircle;
    private BottomSheetBehavior mBottomSheetBehavior;

    private static final int MENU_LOGIN = Menu.FIRST;
    private static final int MENU_ACCOUNT = Menu.FIRST + 1;
    private static final int MENU_BOOKMARKS = Menu.FIRST + 2;
    private static final int MENU_LOGOUT = Menu.FIRST + 3;
    private static final String apiKey = "AIzaSyAQ_BrBZrMNnqrfl8dLUBizFzndCaT5AlY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Initialize Places.
        Places.initialize(getApplicationContext(), apiKey);

// Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

// Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                LatLng latlng = place.getLatLng();
                //Log.d(latlng.latitude+"", latlng.longitude+"");
                if (latlng != null)
                    changeCameraListener(latlng.latitude, latlng.longitude);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        locationSpotList = new ArrayList<>();
        ClientFactory.init(this.getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if(mAuth.getCurrentUser() == null)
            menu.add(0, MENU_LOGIN, Menu.NONE, R.string.login);
        else {
            menu.add(0, MENU_ACCOUNT, Menu.NONE, "Account");
            menu.add(0, MENU_BOOKMARKS, Menu.NONE, "Bookmarks");
            menu.add(0, MENU_LOGOUT, Menu.NONE, R.string.sign_out);

        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case MENU_LOGIN: switchToLogin(); break;
            case MENU_ACCOUNT: break;
            case MENU_BOOKMARKS: switchToBookmarks(); break;
            case MENU_LOGOUT: signOut(); break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void switchToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void switchToBookmarks() {
        startActivity(new Intent(this, BookmarksActivity.class));
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "You have signed out", Toast.LENGTH_SHORT).show();
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
        if (searchCircle != null)
            searchCircle.remove();
        if (areaCircle != null)
            areaCircle.remove();

        CircleOptions searchCircleOptions = new CircleOptions()
                .center(new LatLng(lat, lng)).fillColor(Color.RED).strokeColor(0)
                .radius(20);
        CircleOptions areaCircleOptions = new CircleOptions()
                .center(new LatLng(lat, lng))
                .radius(1000);
        searchCircle = fragmentB.mGoogleMap.addCircle(searchCircleOptions);
        areaCircle = fragmentB.mGoogleMap.addCircle(areaCircleOptions);
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

