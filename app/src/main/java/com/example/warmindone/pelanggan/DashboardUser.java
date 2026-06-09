package com.example.warmindone.pelanggan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.LoginActivity;
import com.example.warmindone.R;
import com.example.warmindone.menuuser.MenuAdapterUser;
import com.example.warmindone.menuuser.MenuModelUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;

public class DashboardUser extends AppCompatActivity {

    private RecyclerView rvBestMenu;
    private RecyclerView rvMakanan;
    private RecyclerView rvMinuman;
    private RecyclerView rvCamilan;

    private ArrayList<MenuModelUser> listBestMenu;
    private ArrayList<MenuModelUser> listMakanan;
    private ArrayList<MenuModelUser> listMinuman;
    private ArrayList<MenuModelUser> listCamilan;

    private MenuAdapterUser adapterBestMenu;
    private MenuAdapterUser adapterMakanan;
    private MenuAdapterUser adapterMinuman;
    private MenuAdapterUser adapterCamilan;

    // Firestore ID Kategori
    private static final String ID_MAKANAN =
            "tSx53XeyQn3o9M39ZHJL";

    private static final String ID_MINUMAN =
            "kdNtZSXnwBWSjEWZHMwz";

    private static final String ID_CAMILAN =
            "C6x9iSSd3RjyFfGhELfZ";

    private TextView tvLokasi;

    private FusedLocationProviderClient
            fusedLocationClient;

    private static final int
            LOCATION_REQUEST = 100;

    private TextView tvProfileCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboarduser);

        rvBestMenu = findViewById(R.id.rvBestMenu);
        rvMakanan = findViewById(R.id.rvMakanan);
        rvMinuman = findViewById(R.id.rvMinuman);
        rvCamilan = findViewById(R.id.rvCamilan);

        listBestMenu = new ArrayList<>();
        listMakanan = new ArrayList<>();
        listMinuman = new ArrayList<>();
        listCamilan = new ArrayList<>();

        adapterBestMenu =
                new MenuAdapterUser(
                        listBestMenu,
                        true);

        adapterMakanan =
                new MenuAdapterUser(
                        listMakanan,
                        false);

        adapterMinuman =
                new MenuAdapterUser(
                        listMinuman,
                        false);

        adapterCamilan =
                new MenuAdapterUser(
                        listCamilan,
                        false);

        rvBestMenu.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        rvMakanan.setLayoutManager(
                new LinearLayoutManager(this));

        rvMinuman.setLayoutManager(
                new LinearLayoutManager(this));

        rvCamilan.setLayoutManager(
                new LinearLayoutManager(this));

        rvBestMenu.setAdapter(adapterBestMenu);
        rvMakanan.setAdapter(adapterMakanan);
        rvMinuman.setAdapter(adapterMinuman);
        rvCamilan.setAdapter(adapterCamilan);

        loadMenu();

        tvLokasi =
                findViewById(
                        R.id.tvLokasi
                );

        fusedLocationClient =
                LocationServices
                        .getFusedLocationProviderClient(
                                this
                        );

        ambilLokasi();

        tvProfileCircle =
                findViewById(
                        R.id.tvProfileCircle
                );

        loadProfile();

        tvProfileCircle =
                findViewById(
                        R.id.tvProfileCircle
                );

        tvProfileCircle.setOnClickListener(
                v -> showProfileMenu(v)
        );
    }

    private void showProfileMenu(View view) {

        PopupMenu popupMenu =
                new PopupMenu(
                        this,
                        view
                );

        popupMenu.getMenu().add(
                "Profile"
        );

        popupMenu.getMenu().add(
                "Logout"
        );

        popupMenu.setOnMenuItemClickListener(item -> {

            if(item.getTitle()
                    .equals("Profile")) {

                Intent intent =
                        new Intent(
                                DashboardUser.this,
                                ProfileActivity.class
                        );

                startActivity(
                        intent
                );

            } else if(item.getTitle()
                    .equals("Logout")) {

                FirebaseAuth
                        .getInstance()
                        .signOut();

                Intent intent =
                        new Intent(
                                DashboardUser.this,
                                LoginActivity.class
                        );

                intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                );

                startActivity(
                        intent
                );

                finish();
            }

            return true;
        });

        popupMenu.show();
    }

    private void loadProfile() {

        String uid =
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser()
                        .getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    String nama =
                            doc.getString(
                                    "nama"
                            );

                    if (nama != null &&
                            !nama.isEmpty()) {

                        tvProfileCircle.setText(
                                nama.substring(
                                        0,
                                        1
                                ).toUpperCase()
                        );
                    }
                });
    }

    private void ambilLokasi() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    LOCATION_REQUEST
            );

            return;
        }

        fusedLocationClient
                .getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location == null) return;

                    try {

                        Geocoder geocoder =
                                new Geocoder(
                                        this,
                                        Locale.getDefault()
                                );

                        List<Address> alamat =
                                geocoder.getFromLocation(
                                        location.getLatitude(),
                                        location.getLongitude(),
                                        1
                                );

                        if (alamat != null &&
                                !alamat.isEmpty()) {

                            String kota =
                                    alamat.get(0)
                                            .getSubAdminArea();

                            if (kota == null) {

                                kota =
                                        alamat.get(0)
                                                .getLocality();
                            }

                            tvLokasi.setText(
                                    kota
                            );
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == LOCATION_REQUEST
                && grantResults.length > 0
                && grantResults[0]
                == PackageManager.PERMISSION_GRANTED) {

            ambilLokasi();
        }
    }

    public void historypage(View view) {
        Intent intent = new Intent(DashboardUser.this, HistoryActivity.class);
        startActivity(intent);
    }

    private void loadMenu() {

        FirebaseFirestore.getInstance()
                .collection("menu")
                .get()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) return;

                    listBestMenu.clear();
                    listMakanan.clear();
                    listMinuman.clear();
                    listCamilan.clear();

                    ArrayList<MenuModelUser> semuaMenu =
                            new ArrayList<>();

                    QuerySnapshot snapshot = task.getResult();

                    for (QueryDocumentSnapshot document :
                            snapshot) {

                        MenuModelUser menu =
                                document.toObject(
                                        MenuModelUser.class);

                        if (menu == null) continue;

                        semuaMenu.add(menu);

                        String kategori =
                                menu.getId_kategori();

                        if (ID_MAKANAN.equals(kategori)) {

                            listMakanan.add(menu);

                        } else if (ID_MINUMAN.equals(kategori)) {

                            listMinuman.add(menu);

                        } else if (ID_CAMILAN.equals(kategori)) {

                            listCamilan.add(menu);
                        }
                    }

                    // BEST MENU RANDOM 3 ITEM
                    Collections.shuffle(semuaMenu);

                    int jumlah =
                            Math.min(3, semuaMenu.size());

                    for (int i = 0; i < jumlah; i++) {
                        listBestMenu.add(
                                semuaMenu.get(i)
                        );
                    }

                    adapterBestMenu.notifyDataSetChanged();
                    adapterMakanan.notifyDataSetChanged();
                    adapterMinuman.notifyDataSetChanged();
                    adapterCamilan.notifyDataSetChanged();
                });
    }
}