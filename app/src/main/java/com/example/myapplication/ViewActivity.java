package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ViewActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        mAuth = FirebaseAuth.getInstance();
        authenticate();
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
}
