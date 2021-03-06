package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.GetLocationInfoQuery;
import com.amazonaws.amplify.generated.graphql.ListLocationInfosQuery;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;

import static android.support.constraint.Constraints.TAG;
import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

public class BottomSheetFrag extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DataCommunication mData;
    private LocationSpotModel locationSpot;
    private LocationInfoModel locationInfo;
    TextView locationNameTextView;
    TextView openCloseTimeTextView;
    TextView locationAddressTextView;
    TextView locationDurationTextView;
    TextView rateTextView;
    //TextView remainingTextView;
    ImageView garageImgImageView;

    String locationName;
    Marker locationMarker;
    String duration;

    ///
    RecyclerView fRecyclerView;
    FloorsAdapter fAdapter;

    ///
    RecyclerView cRecyclerView;
    CommentsAdapter cAdapter;

    private List<Floor> floorList = new ArrayList<>();
    private List<Comment> commentList = new ArrayList<>();

    ///

    @Nullable

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        locationNameTextView = (TextView) view.findViewById(R.id.locationName);
        openCloseTimeTextView = (TextView) view.findViewById(R.id.openCloseTimeText);
        locationAddressTextView = (TextView) view.findViewById(R.id.locationText);
        locationDurationTextView = (TextView) view.findViewById(R.id.locationDuration);
        rateTextView = (TextView) view.findViewById(R.id.rateTextView);
        rateTextView.setSingleLine(false);
        //remainingTextView = (TextView) view.findViewById(R.id.remainingTextView);
        garageImgImageView = (ImageView) view.findViewById(R.id.garageImg);
        Picasso.with(getContext()).load(locationSpot.getGarageImg()).resize(384, 225).into(garageImgImageView);
/*
            new DownloadImageTask((ImageView) view.findViewById(R.id.garageImg))
                    .execute(locationSpot.getGarageImg());
*/

        Button directionButton = view.findViewById(R.id.directionButton);
        Button openCloseTimeButton = view.findViewById(R.id.openCloseTimeButton);

        Button bookmarkButton = view.findViewById(R.id.button_bookmark);
        if (mAuth.getCurrentUser() == null)
            bookmarkButton.setVisibility(View.GONE);
        final EditText commentEditText = view.findViewById(R.id.commentEditText);
        Button submitCommentButton = view.findViewById(R.id.commentButton);
        directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onDirectionClicked(locationMarker);

            }
        });

        getDialog().setOnShowListener(new DialogInterface.OnShowListener(){
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                View bottomSheetInternal = d.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheetInternal).setPeekHeight(200);

                d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

                // Load text
                locationName = locationSpot.getLocationName();
                locationNameTextView.setText(locationName);

                locationDurationTextView.setText(" "+duration.toString());


                locationAddressTextView.setText(locationSpot.getLocationAddress());
                openCloseTimeTextView.setText("Opens "+locationSpot.getOpenCloseTime());
                rateTextView.setText(locationSpot.getParkingRate().replace("\\n", "\n"));
                // Load Img
                    /*
                    try{
                        URL url = new URL(locationSpot.getGarageImg());
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        garageImgImageView.setImageBitmap(bmp);
                    }catch(Exception e)
                    {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "garageImg ERROR:"+locationSpot.getGarageImg());
                        e.printStackTrace();
                    }

*/


                Log.d(TAG, "onShow: locationName: "+ locationName);


            }
        });

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.collection("users").document(mAuth.getCurrentUser().getUid()).collection("bookmarks").document(locationSpot.getLocationId()).set(locationSpot).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(BottomSheetFrag.this.getContext(), "Location bookmarked", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        AnyChartView anyChartView = view.findViewById(R.id.any_chart_view);
        Cartesian cartesian = AnyChart.column();
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("3a", 0.1));
        data.add(new ValueDataEntry("6a", 0.8));
        data.add(new ValueDataEntry("9a", 0.95));
        data.add(new ValueDataEntry("12p", 0.95));
        data.add(new ValueDataEntry("3p", 0.85));
        data.add(new ValueDataEntry("6p", 0.7));
        data.add(new ValueDataEntry("9p", 0.6));
        data.add(new ValueDataEntry("12a", 0.5));
        Column column = cartesian.column(data);
        cartesian.yScale().maximum(1);
        anyChartView.setChart(cartesian);
        fRecyclerView = view.findViewById(R.id.spots_recycler_view);
        fRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        fAdapter = new FloorsAdapter(this.getContext());
        fRecyclerView.setAdapter(fAdapter);
        fAdapter.setItems(locationSpot.getFloors());
        fAdapter.notifyDataSetChanged();
        //getFloors();


        cRecyclerView = view.findViewById(R.id.recycler_view);
        cRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        cAdapter = new CommentsAdapter(this.getContext());
        cRecyclerView.setAdapter(cAdapter);
        getComments();

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null)
            view.findViewById(R.id.insertCommentLayout).setVisibility(View.GONE);
        submitCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String body = commentEditText.getText().toString();
                if (body.length() > 0 && BottomSheetFrag.this.locationSpot != null) {
                    String username = currentUser.getEmail();
                    Comment comment = new Comment(username, body);
                    database.collection("locationSpots").document(BottomSheetFrag.this.locationSpot.getLocationId()).collection("comments").add(comment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(BottomSheetFrag.this.getContext(), "Comment submitted", Toast.LENGTH_SHORT);
                        }
                    });
                    commentEditText.setText("");
                    getComments();
                }
            }
        });
        return view;
    }

    public void setLocationSpot(LocationSpotModel locationSpot) {
        this.locationSpot = locationSpot;
        getLocationInfo();
    }
    /**
    public void getFloors() {

        floorList.add(new Floor("1st Floor", 2, 20));
        floorList.add(new Floor("2nd Floor", 7, 20));
        floorList.add(new Floor("3rd Floor", 14, 14));
        fAdapter.setItems(floorList);
        fAdapter.notifyDataSetChanged();
    }
    **/

    public void getComments() {
        database.collection("locationSpots").document(this.locationSpot.getLocationId()).collection("comments").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                commentList = queryDocumentSnapshots.toObjects(Comment.class);
                cAdapter.setItems(commentList);
                cAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getLocationInfo() {
        Log.d("locationid", "" + this.locationSpot.getLocationId());
        ClientFactory.appSyncClient().query(GetLocationInfoQuery.builder().id(this.locationSpot.getLocationId()).build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(queryCallback);
    }

    private GraphQLCall.Callback<GetLocationInfoQuery.Data> queryCallback = new GraphQLCall.Callback<GetLocationInfoQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<GetLocationInfoQuery.Data> response) {
            if (response.data() != null) {
                locationInfo = new LocationInfoModel(response.data().getLocationInfo());
                locationSpot.setLocationInfo(locationInfo);

                Log.i(TAG, "Retrieved list items: " + locationInfo.toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /**
                        totalTextView.setText("Total spots: " + locationInfo.getTotalSpots());
                        remainingTextView.setText("Remaining spots: " + locationInfo.getRemainingSpots());
                        */
                    }
                });
            }
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("Can't load", e.toString());
        }
    };

    public interface BottomSheetListener{
        void onDirectionClicked(Marker marker);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement Bottom Sheet Listener");
        }

    }
/*
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
*/


}

    /*
    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.activity_maps, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if( behavior != null && behavior instanceof BottomSheetBehavior ) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }
*/


