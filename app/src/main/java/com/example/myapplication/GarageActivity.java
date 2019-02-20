package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;


import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.support.v7.widget.SearchView;
import android.support.v4.view.MenuItemCompat;
import android.app.SearchManager;
import android.widget.EditText;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.Spanned;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class GarageActivity extends AppCompatActivity {


    private static final String TAG = "GarageActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;


    private RecyclerView recyclerView;

    // @BindView(R.id.recycler_view)
    // RecyclerView recyclerView;

    //@BindView(R.id.toolbar)
    //Toolbar toolbar;
    private Toolbar toolbar;

    private RecyclerViewAdapter mAdapter;

    private ArrayList<AbstractModel> modelList = new ArrayList<>();

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mAuth = FirebaseAuth.getInstance();
        authenticate();
        isServiceOK();
        // ButterKnife.bind(this);
        findViews();
        initToolbar("Parking Space");
        setAdapter();


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

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    public void initToolbar(String title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_search, menu);


        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(menu.findItem(R.id.action_search));

        SearchManager searchManager = (SearchManager) this.getSystemService(this.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));

        //changing edittext color
        EditText searchEdit = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        searchEdit.setTextColor(Color.WHITE);
        searchEdit.setHintTextColor(Color.WHITE);
        searchEdit.setBackgroundColor(Color.TRANSPARENT);
        searchEdit.setHint("Search");

        InputFilter[] fArray = new InputFilter[2];
        fArray[0] = new InputFilter.LengthFilter(40);
        fArray[1] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                for (int i = start; i < end; i++) {

                    if (!Character.isLetterOrDigit(source.charAt(i)))
                        return "";
                }


                return null;


            }
        };
        searchEdit.setFilters(fArray);
        View v = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        v.setBackgroundColor(Color.TRANSPARENT);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<AbstractModel> filterList = new ArrayList<AbstractModel>();
                if (s.length() > 0) {
                    for (int i = 0; i < modelList.size(); i++) {
                        if (modelList.get(i).getTitle().toLowerCase().contains(s.toString().toLowerCase())) {
                            filterList.add(modelList.get(i));
                            mAdapter.updateList(filterList);
                        }
                    }

                } else {
                    mAdapter.updateList(modelList);
                }
                return false;
            }
        });


        return true;
    }


    private void setAdapter() {


        modelList.add(new AbstractModel("North Garage", "500/1500 " + " Parking Space Used"));
        modelList.add(new AbstractModel("South Garage", "1000/1500 " + " Parking Space Used"));
        modelList.add(new AbstractModel("West Garage", "1500/1500 " + " Parking Space Used"));

        mAdapter = new RecyclerViewAdapter(GarageActivity.this, modelList);

        recyclerView.setHasFixedSize(true);

        // use a linear layout manager

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);


        recyclerView.setAdapter(mAdapter);


        mAdapter.SetOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, AbstractModel model) {

                //handle item click events here
                //Toast.makeText(GarageActivity.this, "Hey " + model.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), MapsActivity.class);
                startActivity(intent);


            }
        });


    }

    // Checks Google Play Version
    public void isServiceOK(){
        Log.d(TAG,"isServicesOK: Checking Google Services Version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(GarageActivity.this);

        if(available == ConnectionResult.SUCCESS)
        {
            Log.d(TAG,"isServicesOK: Google Play Services is working");
          //  return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {
            Log.d(TAG,"isServicesOK: an error occured");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(GarageActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else
        {
            Toast.makeText(this,"You can't make map requests", Toast.LENGTH_SHORT).show();
        }
      //  return false;
    }


    public void switchToAuth(View view) {
        startActivity(new Intent(this, AuthorizationActivity.class));
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        authenticate();
    }
}
