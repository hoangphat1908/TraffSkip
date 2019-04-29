package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookmarksActivity extends AppCompatActivity {
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    RecyclerView mRecyclerView;
    BookmarksAdapter mAdapter;
    private List<LocationSpotModel> locationList = new ArrayList<>();
    private static final String TAG = "BookmarksActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        mRecyclerView = findViewById(R.id.recycler_view);
        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // specify an adapter (see also next example)
        mAdapter = new BookmarksAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        getBookmarks();
    }

    public void getBookmarks() {
        database.collection("users").document(this.mAuth.getCurrentUser().getUid()).collection("bookmarks").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                locationList = queryDocumentSnapshots.toObjects(LocationSpotModel.class);
                mAdapter.setItems(locationList);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
