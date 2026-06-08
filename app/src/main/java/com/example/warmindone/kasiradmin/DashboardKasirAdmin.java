package com.example.warmindone.kasiradmin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.warmindone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.example.warmindone.LoginActivity;

import android.content.Intent;
import android.view.View;

public class DashboardKasirAdmin extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboardkasiradmin);
    }

    public void kategoripage(View view) {
        Intent intent = new Intent(DashboardKasirAdmin.this, KategoriActivity.class);
        startActivity(intent);
    }

    public void menupage(View view) {
        Intent intent = new Intent(DashboardKasirAdmin.this, MenuActivity.class);
        startActivity(intent);
    }

    public void incomepage(View view) {
        Intent intent = new Intent(DashboardKasirAdmin.this, PendapatanActivity.class);
        startActivity(intent);
    }
    public void pesananpage(View view) {
        Intent intent = new Intent(DashboardKasirAdmin.this, PesananBelumActivity.class);
        startActivity(intent);
    }
    public void userpage(View view) {
        Intent intent = new Intent(DashboardKasirAdmin.this, UserActivity.class);
        startActivity(intent);
    }
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(DashboardKasirAdmin.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
