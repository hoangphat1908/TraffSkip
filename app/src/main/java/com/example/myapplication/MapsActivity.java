package com.example.myapplication;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.GeoApiContext;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;






public class MapsActivity extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private RelativeLayout mMapContainer;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    static Location lastLocation = null;
// for testing
    private static float origin = 0;
    private static float destination = 50;
    //
    private static final float DESTINATION = destination;
    private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private RecyclerView mRecyclerView;

    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private int mMapLayoutState = 0;

    private LatLngBounds mMapBoundary;

    private GeoApiContext mGeoApiContext = null;

    private Boolean mLocationPermissionGranted = false;

    String serverKey= "AIzaSyByWXCj67x1VrBrh_7NuUS4fRkRnIDGPys";


    //protected
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_maps);

      getLocationPermission();


        /*
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        */


        //setHasOptionsMenu(true);

        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }


    }
@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancesState)
{
    View view = inflater.inflate(R.layout.activity_maps,container,false);
    mRecyclerView = view.findViewById(R.id.recycler_view_activity);
    mMapContainer = view.findViewById(R.id.map_container);
    mMapView = (MapView) view.findViewById(R.id.user_list_map);

    view.findViewById(R.id.btn_full_screen_map).setOnClickListener(this);

    Bundle mapViewBundle = null;
    if(savedInstancesState != null)
    {
        mapViewBundle = savedInstancesState.getBundle(MAPVIEW_BUNDLE_KEY);
    }
    mMapView.onCreate(mapViewBundle);

    mMapView.getMapAsync(this);

    return view;
}
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap map) {

        Log.d(TAG, "onMapReady: Map is ready");


        // Test
        addMapMarker(map,37.338787,-121.880388,"Hello world");
        addMapMarker(map,37.334157,-121.882031,"Hello");


        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            map.setMyLocationEnabled(true);
            mGoogleMap = map;
        }

    }

    private void addMapMarker ( GoogleMap mMap, double lat, double lng, String info)
    {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat,lng))
                .title(info));
    }



    // Checks and grants permission for Location.
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting lcoation permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION
        };
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult: called");

        mLocationPermissionGranted = false;


        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "onRequestPermissionsResult: permission granted");

                mLocationPermissionGranted = true;
            }
        } else {
            mLocationPermissionGranted = false;
            Log.d(TAG, "onRequestPermissionsResult: permission failed");
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting device lcoation");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {

            if (mLocationPermissionGranted) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                            }
                        }
                        else {
                            Log.d(TAG, "onComplete:  failed");
                        }
                    }
                });
            }

        }catch (SecurityException e)
        {
            Log.d(TAG, "getDeviceLocation: Security Exception:"+e.getMessage());
        }
    }

    // moving the camera to user location
    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: camera to self lat:"+ latLng.latitude + ", lng: "+ latLng.longitude);
     //  mGoogleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps_activity)).getMap();

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    private void displayDirection(LatLng point)
    {
        // clear map
        mGoogleMap.clear();

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        options.position(point);

        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    }

    private void expandMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                50,
                100);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mRecyclerView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                50,
                0);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    private void contractMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                100,
                50);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mRecyclerView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                0,
                50);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_full_screen_map:{
                if(mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED)
                {
                    mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
                    expandMapAnimation();
                }
                else if(mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED)
                {
                    mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
                    contractMapAnimation();
                }
                break;
            }
        }
    }





}
