package com.example.myapplication;

import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.Status;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import android.widget.Toast;

public class MapsActivity extends Fragment implements OnMapReadyCallback, View.OnClickListener,  GoogleMap.OnPolylineClickListener,GoogleMap.OnInfoWindowClickListener {

    // The map is loading first in order, and ViewActivity last, so I have to load the database here for the map to use it initially.
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    DataCommunication mData;
    public GoogleMap mGoogleMap;
    private MapView mMapView;
    private RelativeLayout mMapContainer;
    private String tempDuration;

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

    private Location currentLocation;
    private ArrayList<PolylineData> polylineData = new ArrayList<>();
    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private int mMapLayoutState = 0;
    private Marker selectedMarker = null;
    private ArrayList<Marker> TripMarkers = new ArrayList<>();

    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();


    private List<LocationSpotModel> locationSpotList = new ArrayList<LocationSpotModel>();
    //


    private LatLngBounds mMapBoundary;

    //For customizing marker, todo
    private Marker myMarker;

    private GeoApiContext mGeoApiContext = null;

    private Boolean mLocationPermissionGranted = false;


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

    @Override
    public void onMapReady(GoogleMap map) {




        Log.d(TAG, "onMapReady: Map is ready");

        if (map != null) {


        createLocationSpotList(map);

            map.setOnInfoWindowClickListener(this);

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
            map.setOnPolylineClickListener(this);
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


    private void createLocationSpotList(final GoogleMap map) {



        database.collection("locationSpots").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                locationSpotList = queryDocumentSnapshots.toObjects(LocationSpotModel.class);

                Log.d(TAG, "createLocationSpotList:  onSuccess Called:  size: "+locationSpotList.size());

                addMapMarkers(map);


            }
        });

            Log.d(TAG, "createLocationSpotList: Error Exception");


       // locationSpotList = mData.getlocationSpotList();

        Log.d(TAG, "createLocationSpotList: called, size: "+locationSpotList.size());

    }

    // add group of markers here for loading groups of them
    private void addMapMarkers(GoogleMap map) {

        for (int i = 0; i < locationSpotList.size(); i++)
        {
            Log.d(TAG, "addMapMarkers: locationSpotList "+ i+", locationName: "+locationSpotList.get(i).getLocationName());
            
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(locationSpotList.get(i).getLatlng().getLatitude(), locationSpotList.get(i).getLatlng().getLongitude()))
                    .title(locationSpotList.get(i).getLocationName())
                    .snippet(locationSpotList.get(i).getLocationName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            );

            Log.d(TAG, "creatingLocationSpot: "+i);
            Log.d(TAG, "locationSpotList Size : "+locationSpotList.size());

            //final int locationSpotIndex=i;

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    resetSelectedMarker();
                    BottomSheetFrag infoSheet = new BottomSheetFrag();
                   // calculateDirections(marker);

                    // TODO: Could probably find a better way to do this for syncing
                    // Problem - Calculating the Duration time takes a while, so infoSheet.Duration = null in meantime.
                    // tempDuration = null; also lags, so clicking on a different marker does not completely resets until later.
                    while(tempDuration == null)
                    {
                        calculateDuration(marker);
                        infoSheet.duration = tempDuration;
                        Log.d(TAG, "onMarkerClick: TempDuration " + tempDuration);
                    }

               //     calculateDuration(marker);

                    infoSheet.duration = tempDuration;
                    Log.d(TAG, "onMarkerClick: TempDuration " + tempDuration);

                    Integer locationSpotIndex = mHashMap.get(marker);



                    //Create Bottom Sheet when marker clicked


                    infoSheet.locationMarker = marker;


 //                   Log.d(TAG, "onMarkerClick: TempDuration " + tempDuration);

//                    Log.d(TAG, "addMapMarkers: locationSpotList "+ locationSpotIndex+", locationName: "+locationSpotList.get(locationSpotIndex).getLocationName());


                    infoSheet.setLocationSpot(locationSpotList.get(locationSpotIndex));

                    infoSheet.show(getActivity().getSupportFragmentManager(),"infoSheet");

                  //  tempDuration = null;


                    selectedMarker = marker;
                    Log.d(TAG, "onMarkerClick: onClick Tested");

                    tempDuration = null;
                    // Triggered when user click any marker on the map
                    return false;
                }
            });

            mHashMap.put(marker, i);
            Log.d(TAG, "addMapMarkers: called");
          //  Log.d(TAG, "hashMap: i : "+i);
           // Log.d(TAG, "hashMap: i : "+mHashMap.get('1'));
        }

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


    // TODO: marker # not completely reset

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


    public void calculateDuration(Marker marker)
    {

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true); // shows all possible routes
        directions.origin(
                new com.google.maps.model.LatLng(
                        currentLocation.getLatitude(),
                        currentLocation.getLongitude()
                )
        );

        // Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDuration: duration: " + result.routes[0].legs[0].duration);


               tempDuration = result.routes[0].legs[0].duration.toString();

            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDuration: Failed to get directions: " + e.getMessage());

            }
        });

    }

    public void calculateDirections(Marker marker) {
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


    @Override
    public void onInfoWindowClick(final Marker marker) {
        if(marker.getTitle().contains("Trip")){

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Open Google Maps?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            String latitude = String.valueOf(marker.getPosition().latitude);
                            String longitude = String.valueOf(marker.getPosition().longitude);
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");

                            try{
                                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    startActivity(mapIntent);
                                }
                            }catch (NullPointerException e){
                                Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                                Toast.makeText(getActivity(), "Couldn't open map", Toast.LENGTH_SHORT).show();
                            }

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
        else{
            if(marker.getSnippet().equals("This is you")){
                marker.hideInfoWindow();
            }
            else{

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(marker.getSnippet())
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                resetSelectedMarker();
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

    }
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