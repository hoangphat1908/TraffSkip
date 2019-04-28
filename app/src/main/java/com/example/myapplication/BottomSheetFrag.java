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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;

import static android.support.constraint.Constraints.TAG;

public class BottomSheetFrag extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    DataCommunication mData;
    private LocationSpotModel locationSpot;
    TextView locationNameTextView;
    TextView openCloseTimeTextView;
    TextView locationAddressTextView;
    TextView locationDurationTextView;
    ImageView garageImgImageView;

    String locationName;
    Marker locationMarker;
    String duration;



        @Nullable

        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

            locationNameTextView = (TextView) view.findViewById(R.id.locationName);
            openCloseTimeTextView = (TextView) view.findViewById(R.id.openCloseTimeText);
            locationAddressTextView = (TextView) view.findViewById(R.id.locationText);
            locationDurationTextView = (TextView) view.findViewById(R.id.locationDuration);

            garageImgImageView = (ImageView) view.findViewById(R.id.garageImg);

            Picasso.with(getContext()).load(locationSpot.getGarageImg()).resize(384, 225).into(garageImgImageView);
/*
            new DownloadImageTask((ImageView) view.findViewById(R.id.garageImg))
                    .execute(locationSpot.getGarageImg());
*/
            Button directionButton = view.findViewById(R.id.directionButton);
            Button openCloseTimeButton = view.findViewById(R.id.openCloseTimeButton);

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

            return view;
        }


        public void setLocationSpot(LocationSpotModel locationSpot)
        {
            this.locationSpot = locationSpot;
        }


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


