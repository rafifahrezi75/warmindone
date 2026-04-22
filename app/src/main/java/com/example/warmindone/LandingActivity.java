package com.example.warmindone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landingpg);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(LandingActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }, 3000); // 3 detik
    }
}