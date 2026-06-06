package com.example.warmindone.pelanggan;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;
import com.example.warmindone.menuuser.MenuAdapterUser;
import com.example.warmindone.menuuser.MenuModelUser;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

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