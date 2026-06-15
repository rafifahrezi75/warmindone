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
import android.widget.LinearLayout;
import android.widget.Button;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import java.util.LinkedHashMap;
import java.util.Map;
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

    private EditText etSearch;
    private ArrayList<MenuModelUser> allMenu = new ArrayList<>();

    // Firestore ID Kategori
    private static final String ID_MAKANAN =
            "tSx53XeyQn3o9M39ZHJL";

    private static final String ID_MINUMAN =
            "kdNtZSXnwBWSjEWZHMwz";

    private static final String ID_CAMILAN =
            "C6x9iSSd3RjyFfGhELfZ";

    private TextView tvLokasi;

    private LinearLayout layoutLokasiDetail;

    private final Map<String, String[]> dataLokasi =
            new LinkedHashMap<>();
    private FusedLocationProviderClient
            fusedLocationClient;

    private static final int
            LOCATION_REQUEST = 100;

    private TextView tvProfileCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboarduser);

        Button btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(v -> {
            filterMenu(etSearch.getText().toString());
        });

        rvBestMenu = findViewById(R.id.rvBestMenu);
        rvMakanan = findViewById(R.id.rvMakanan);
        rvMinuman = findViewById(R.id.rvMinuman);
        rvCamilan = findViewById(R.id.rvCamilan);

        etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMenu(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

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

        layoutLokasiDetail =
                findViewById(R.id.layoutLokasiDetail);

        initDataLokasi();

        loadSavedLocation();

        layoutLokasiDetail.setOnClickListener(
                v -> showProvinsiDialog()
        );

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        SharedPreferences prefs =
                getSharedPreferences(
                        "warmindone",
                        MODE_PRIVATE
                );

        String lokasiTersimpan =
                prefs.getString(
                        "lokasi",
                        null
                );

        if (lokasiTersimpan == null) {
            ambilLokasi();
        }
        tvProfileCircle =
                findViewById(
                        R.id.tvProfileCircle
                );

        loadProfile();

        tvProfileCircle.setOnClickListener(
                v -> showProfileMenu(v)
        );
    }

    private void filterMenu(String keyword) {

        String query = keyword.toLowerCase().trim();

        if (query.isEmpty()) {
            loadMenu();
            return;
        }

        listBestMenu.clear();
        listMakanan.clear();
        listMinuman.clear();
        listCamilan.clear();

        for (MenuModelUser menu : allMenu) {

            if (menu.getNama_menu() != null &&
                    menu.getNama_menu().toLowerCase().contains(query)) {

                listBestMenu.add(menu);

                String kategori = menu.getId_kategori();

                if (ID_MAKANAN.equals(kategori)) {
                    listMakanan.add(menu);

                } else if (ID_MINUMAN.equals(kategori)) {
                    listMinuman.add(menu);

                } else if (ID_CAMILAN.equals(kategori)) {
                    listCamilan.add(menu);
                }
            }
        }

        updateUI();
    }

    private void updateUI() {
        adapterBestMenu.notifyDataSetChanged();
        adapterMakanan.notifyDataSetChanged();
        adapterMinuman.notifyDataSetChanged();
        adapterCamilan.notifyDataSetChanged();
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

    private void initDataLokasi() {

        dataLokasi.put(
                "Jawa Timur",
                new String[]{
                        "Surabaya",
                        "Sidoarjo",
                        "Gresik",
                        "Malang",
                        "Mojokerto",
                        "Jombang",
                        "Kediri",
                        "Blitar",
                        "Madiun",
                        "Banyuwangi",
                        "Probolinggo",
                        "Pasuruan",
                        "Lamongan",
                        "Tuban",
                        "Bojonegoro"
                }
        );

        dataLokasi.put(
                "DKI Jakarta",
                new String[]{
                        "Jakarta Pusat",
                        "Jakarta Barat",
                        "Jakarta Timur",
                        "Jakarta Selatan",
                        "Jakarta Utara"
                }
        );

        dataLokasi.put(
                "Jawa Barat",
                new String[]{
                        "Bandung",
                        "Bekasi",
                        "Bogor",
                        "Depok",
                        "Cimahi",
                        "Cirebon",
                        "Sukabumi",
                        "Tasikmalaya"
                }
        );

        dataLokasi.put(
                "Jawa Tengah",
                new String[]{
                        "Semarang",
                        "Solo",
                        "Salatiga",
                        "Magelang",
                        "Pekalongan",
                        "Tegal"
                }
        );

        dataLokasi.put(
                "Bali",
                new String[]{
                        "Denpasar",
                        "Badung",
                        "Gianyar",
                        "Tabanan",
                        "Karangasem"
                }
        );
    }

    private void showProvinsiDialog() {

        String[] provinsiAsli =
                dataLokasi.keySet()
                        .toArray(new String[0]);

        String[] menu =
                new String[provinsiAsli.length + 1];

        menu[0] = "📍 Gunakan Lokasi GPS";

        for (int i = 0; i < provinsiAsli.length; i++) {
            menu[i + 1] = provinsiAsli[i];
        }

        new AlertDialog.Builder(this)
                .setTitle("Pilih Lokasi")
                .setItems(menu, (dialog, which) -> {

                    // Opsi GPS
                    if (which == 0) {

                        ambilLokasi();

                        return;
                    }

                    // Opsi Provinsi
                    showKotaDialog(
                            provinsiAsli[which - 1]
                    );
                })
                .show();
    }

    private void showKotaDialog(
            String provinsi) {

        String[] kota =
                dataLokasi.get(provinsi);

        new AlertDialog.Builder(this)
                .setTitle("Pilih Kota")
                .setItems(kota,
                        (dialog, which) -> {

                            tvLokasi.setText(
                                    kota[which]
                            );

                            saveLocation(
                                    kota[which]
                            );
                        })
                .show();
    }

    private void saveLocation(
            String lokasi) {

        SharedPreferences prefs =
                getSharedPreferences(
                        "warmindone",
                        MODE_PRIVATE
                );

        prefs.edit()
                .putString(
                        "lokasi",
                        lokasi
                )
                .apply();
    }

    private void loadSavedLocation() {

        SharedPreferences prefs =
                getSharedPreferences(
                        "warmindone",
                        MODE_PRIVATE
                );

        String lokasi =
                prefs.getString(
                        "lokasi",
                        null
                );

        if(lokasi != null) {

            tvLokasi.setText(
                    lokasi
            );
        }
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

                            if (kota != null && !kota.trim().isEmpty()) {

                                tvLokasi.setText(kota);

                                saveLocation(kota);
                            }
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
        // 1. Ambil data penjualan dari orders_detail terlebih dahulu
        FirebaseFirestore.getInstance()
                .collection("orders_detail")
                .get()
                .addOnCompleteListener(taskDetail -> {
                    if (!taskDetail.isSuccessful()) return;

                    Map<String, Long> counterMenuTerjual = new LinkedHashMap<>();

                    for (QueryDocumentSnapshot detailDoc : taskDetail.getResult()) {
                        String idMenu = detailDoc.getString("id_menu");
                        Long jumlah = detailDoc.getLong("jumlah");

                        if (idMenu != null && jumlah != null) {
                            long totalLama = counterMenuTerjual.containsKey(idMenu) ? counterMenuTerjual.get(idMenu) : 0L;
                            counterMenuTerjual.put(idMenu, totalLama + jumlah);
                        }
                    }

                    // 2. Ambil data master menu
                    FirebaseFirestore.getInstance()
                            .collection("menu")
                            .get()
                            .addOnCompleteListener(taskMenu -> {
                                if (!taskMenu.isSuccessful()) return;

                                allMenu.clear();
                                listBestMenu.clear();
                                listMakanan.clear();
                                listMinuman.clear();
                                listCamilan.clear();

                                QuerySnapshot snapshot = taskMenu.getResult();

                                for (QueryDocumentSnapshot document : snapshot) {
                                    MenuModelUser menu = document.toObject(MenuModelUser.class);
                                    if (menu == null) continue;

                                    // Ambil ID dan Kategori langsung dari dokumen Firestore
                                    String idDocument = document.getId();
                                    String kategori = document.getString("id_kategori");

                                    allMenu.add(menu);

                                    // Pengelompokan Kategori
                                    if (ID_MAKANAN.equals(kategori)) {
                                        listMakanan.add(menu);
                                    } else if (ID_MINUMAN.equals(kategori)) {
                                        listMinuman.add(menu);
                                    } else if (ID_CAMILAN.equals(kategori)) {
                                        listCamilan.add(menu);
                                    }

                                    // FILTER BEST MENU
                                    long totalTerjual = counterMenuTerjual.containsKey(idDocument) ? counterMenuTerjual.get(idDocument) : 0L;
                                    if (totalTerjual > 5) {
                                        listBestMenu.add(menu);
                                    }
                                }

                                // Variasi susunan Best Menu
                                Collections.shuffle(listBestMenu);

                                // Batasi maksimal tampil 3 item list horizontal
                                if (listBestMenu.size() > 3) {
                                    listBestMenu = new ArrayList<>(listBestMenu.subList(0, 3));
                                }

                                // Pasang data baru ke semua Adapter UI
                                adapterBestMenu.notifyDataSetChanged();
                                adapterMakanan.notifyDataSetChanged();
                                adapterMinuman.notifyDataSetChanged();
                                adapterCamilan.notifyDataSetChanged();
                            });
                });
    }
}