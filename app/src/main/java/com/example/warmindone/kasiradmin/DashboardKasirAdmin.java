package com.example.warmindone.kasiradmin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.warmindone.R;

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
}
