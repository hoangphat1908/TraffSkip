package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

public class AuthorizationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_authorization);

    }

    public void switchToLogIn(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void switchToCreateAccount(View view) {
        startActivity(new Intent(this, CreateAccountActivity.class));
    }
}
