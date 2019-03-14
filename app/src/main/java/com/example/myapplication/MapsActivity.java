package com.example.myapplication;

import android.app.AlertDialog;
import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class MapsActivity extends Fragment implements OnMapReadyCallback, View.OnClickListener,  GoogleMap.OnPolylineClickListener {


    DataCommunication mData;
    public GoogleMap mGoogleMap;
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
    private Location currentLocation;
    private ArrayList<PolylineData> polylineData = new ArrayList<>();
    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private int mMapLayoutState = 0;
    private Marker selectedMarker = null;
    private ArrayList<Marker> TripMarkers = new ArrayList<>();

    private HashMap<Integer, Marker> mHashMap = new HashMap<Integer, Marker>();
    private ArrayList<RecyclerViewFragmentAbstractModel> modelList = new ArrayList<RecyclerViewFragmentAbstractModel>();

    private LatLngBounds mMapBoundary;

    //For customizing marker, todo
    private Marker myMarker;

    private GeoApiContext mGeoApiContext = null;

    private Boolean mLocationPermissionGranted = false;

    //  String serverKey= "AIzaSyAyVpB5uv369pOP7LpmuqUGyxV_aNon20g";

    public MapsActivity()
    {

    }

    //protected
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_maps);

        getLocationPermission();


        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancesState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view_activity);
        mMapContainer = view.findViewById(R.id.map_container);
        mMapView = (MapView) view.findViewById(R.id.user_list_map);

        view.findViewById(R.id.btn_reset_map).setOnClickListener(this);

        initGoogleMap(savedInstancesState);

        Bundle mapViewBundle = null;
        if (savedInstancesState != null) {
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

        if (map != null) {
            createRecyclerViewList();
            addMapMarkers(map);

            mData.setMarkerList(mHashMap);

        }
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
            mGoogleMap.setOnPolylineClickListener(this);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mData = (DataCommunication) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DataCommunication");
        }
    }

    //recreating for map usage, gets the modelList from RecyclerView Fragment.
    private void createRecyclerViewList() {
        modelList = mData.getModelList();

    }

    // add group of markers here for loading groups of them
    private void addMapMarkers(GoogleMap map) {

        for (int i = 0; i < modelList.size(); i++) {
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(modelList.get(i).getLat(), modelList.get(i).getLng()))
                    .title(modelList.get(i).getItemName())
                    .snippet(modelList.get(i).getItemName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            );

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    resetSelectedMarker();
                    calculateDirections(marker);
                    selectedMarker = marker;
                    Log.d(TAG, "onMarkerClick: onClick Tested");

                    // Triggered when user click any marker on the map
                    return false;
                }
            });

            mHashMap.put(i, marker);

          //  Log.d(TAG, "hashMap: i : "+i);
           // Log.d(TAG, "hashMap: i : "+mHashMap.get('1'));
        }

    }

    private void addMapMarker(GoogleMap mMap, double lat, double lng, String info, String snippet) {


        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(info)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        );

        // mMap.setOnMarkerClickListener(this);
        //  mGoogleMap.setOnInfoWindowClickListener(this);

        /*
       mGoogleMap.setOnInfoWindowClickListener(new mGoogleMap.OnInfoWindowClickListener(){
        @Override
        public void onInfoWindowClick(Marker marker) {



        //send data to citygallery activity
        Intent intent = new Intent(getContext(), CItyGalleryActivity.class);
        intent.putExtra("cityChoise",picCities.get(0).get_city().toString());
        startActivity(intent);



    }
    }

       );
*/

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                resetSelectedMarker();
                calculateDirections(marker);
                selectedMarker = marker;
                Log.d(TAG, "onMarkerClick: onClick Tested");
                // Triggered when user click any marker on the map
                return false;
            }
        });


    }

    private void resetMap() {
        if (mGoogleMap != null) {
            mGoogleMap.clear();


            if (polylineData.size() > 0) {
                polylineData.clear();
                polylineData = new ArrayList<>();
            }
        }
    }


    // TODO: marker not completely reset

    private void addPolylinesToMap(final DirectionsResult result) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);
                // Duration is for determining the fastest route
                double duration = 99999;

                if (polylineData.size() > 0) {
                    for (PolylineData polylineData : polylineData) {
                        polylineData.getPolyline().remove();
                    }
                }

                for (DirectionsRoute route : result.routes) {
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (com.google.maps.model.LatLng latLng : decodedPath) {

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mGoogleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    //  polyline.setColor(ContextCompat.getColor(getActivity(), ContextCompat.getColor(getResources(), R.color.idOfColour)
                    polyline.setColor(Color.GRAY);
                    polyline.setClickable(true);
                    polylineData.add(new PolylineData(polyline, route.legs[0]));

                    double tempDuration = route.legs[0].duration.inSeconds;
                    if (tempDuration < duration) {
                        duration = tempDuration;
                        onPolylineClick(polyline);
                        zoomRoute(polyline.getPoints());
                    }


                    selectedMarker.setVisible(false);
                }
            }
        });
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
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                        } else {
                            Log.d(TAG, "onComplete:  failed");
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: Security Exception:" + e.getMessage());
        }
    }

    // moving the camera to user location
    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: camera to self lat:" + latLng.latitude + ", lng: " + latLng.longitude);
        //  mGoogleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps_activity)).getMap();

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void displayDirection(LatLng point) {
        // clear map
        mGoogleMap.clear();

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        options.position(point);

        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    }

    private void expandMapAnimation() {
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

    private void contractMapAnimation() {
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

    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
        if (mGeoApiContext == null) {
            if (mGeoApiContext == null) {
                mGeoApiContext = new GeoApiContext.Builder().apiKey("AIzaSyAQ_BrBZrMNnqrfl8dLUBizFzndCaT5AlY").build();
            }
            //AIzaSyAyVpB5uv369pOP7LpmuqUGyxV_aNon20g
            //AIzaSyAQ_BrBZrMNnqrfl8dLUBizFzndCaT5AlY
        }
    }

    private void calculateDirections(Marker marker) {
        Log.d(TAG, "calculateDirections: calculating directions.");


        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        Log.d(TAG, "marker latitude" + marker.getPosition().latitude);

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true); // shows all possible routes
        directions.origin(
                new com.google.maps.model.LatLng(
                        //  mUserPosition.getGeo_point().getLatitude(),
                        // mUserPosition.getGeo_point().getLongitude()
                        currentLocation.getLatitude(),
                        currentLocation.getLongitude()
                )
        );
        Log.d(TAG, "Current latitude" + currentLocation.getLatitude());

        // Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                addPolylinesToMap(result);
                //marker.add(marker);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage());

            }
        });
    }

// OnInfoWindowClickListenerCallback
    //googleMap.setOnInfoWindowClickListener(listener)

    /*
@Override
    public void onInfoWindowClick(final Marker marker) {
        if(marker.getSnippet().equals("This is you")){
            marker.hideInfoWindow();
        }
        else{

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(marker.getSnippet())
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            Log.d(TAG, "onClick: OnInfoWindowClick reached");
                           selectedMarker = marker;
                            calculateDirections(marker);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset_map: {
                resetMap();
                addMapMarkers(mGoogleMap);
                break;
            }

        }

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

    /*
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
*/

    @Override
    public void onPolylineClick(Polyline polyline) {
        int index = 0;
        for (PolylineData polylineData : polylineData) {
            index++;
            Log.d(TAG, "onPolylineClick: reached");
            if (polyline.getId().equals((polylineData.getPolyline().getId()))) {
                polylineData.getPolyline().setColor(Color.rgb(135, 206, 250));
                polylineData.getPolyline().setZIndex(1);

                LatLng finaldest = new LatLng(
                        polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );

                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(finaldest)
                        .title("Trip: #" + index)
                        .snippet("Duration: " + polylineData.getLeg().duration)
                );
                marker.showInfoWindow();
                TripMarkers.add(marker);
            } else {
                polylineData.getPolyline().setColor(Color.GRAY);
                polylineData.getPolyline().setZIndex(0);
            }
        }

    }

    //  remove old trip markers when clicking somewhere else.
    private void removeTripMarkers() {

        for (Marker marker : TripMarkers) {
            marker.remove();
        }
    }

    private void resetSelectedMarker() {
        if (selectedMarker != null) {
            selectedMarker.setVisible(true);
            selectedMarker = null;
            removeTripMarkers();
        }
    }


    // Zoom map when marker clicked
    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mGoogleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mGoogleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }


}