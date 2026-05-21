package com.example.warmindone.kasiradmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.warmindone.R;
import com.example.warmindone.addon.AddonAdapter;
import com.example.warmindone.addon.AddonModel;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DetailMenuActivity extends AppCompatActivity {

    private RecyclerView recyclerAddon;
    private ImageView btnBack, imgMenu;
    private TextView tvNamaMenu, tvKategori, tvHarga, tvStok, tvDeskripsi;
    private FloatingActionButton btnTambahAddon;
    
    private ArrayList<AddonModel> list;
    private AddonAdapter adapter;
    private FirebaseFirestore db;
    private String menuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailmenu);

        // INIT VIEW
        recyclerAddon = findViewById(R.id.recyclerAddon);
        btnBack = findViewById(R.id.btnBack);
        imgMenu = findViewById(R.id.imgMenu);
        tvNamaMenu = findViewById(R.id.tvNamaMenu);
        tvKategori = findViewById(R.id.tvKategori);
        tvHarga = findViewById(R.id.tvHarga);
        tvStok = findViewById(R.id.tvStok);
        tvDeskripsi = findViewById(R.id.tvDeskripsi);
        btnTambahAddon = findViewById(R.id.btnTambahAddon);

        db = FirebaseFirestore.getInstance();
        menuId = getIntent().getStringExtra("id");

        btnBack.setOnClickListener(v -> finish());

        // RECYCLER SETTINGS
        recyclerAddon.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new AddonAdapter(this, list);
        recyclerAddon.setAdapter(adapter);

        btnTambahAddon.setOnClickListener(v -> {
            Intent intent = new Intent(this, TambahAddonActivity.class);
            intent.putExtra("menuId", menuId);
            startActivity(intent);
        });

        if (menuId != null) {
            getDetailMenu();
        }
    }

    private void getDetailMenu() {
        db.collection("menu").document(menuId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        tvNamaMenu.setText(doc.getString("nama_menu"));
                        tvDeskripsi.setText(doc.getString("deskripsi"));
                        long harga = doc.getLong("harga") != null ? doc.getLong("harga") : 0;
                        tvHarga.setText("Rp. " + String.format("%,d", harga));
                        tvStok.setText("Stok : " + doc.getLong("stok"));

                        Glide.with(this).load(doc.getString("foto"))
                                .placeholder(R.drawable.ic_menu).into(imgMenu);

                        String katId = doc.getString("id_kategori");
                        if (katId != null) {
                            db.collection("kategori").document(katId).get()
                                    .addOnSuccessListener(kDoc -> {
                                        if (kDoc.exists()) tvKategori.setText(kDoc.getString("kategori"));
                                    });
                        }
                    }
                });
    }

    private void loadAddonData() {
        // Ambil relasi dari menu_addon
        db.collection("menu_addon")
                .whereEqualTo("id_menu", menuId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Bersihkan list hanya sekali di awal fetch baru
                    list.clear(); 
                    adapter.notifyDataSetChanged();
                    
                    if (queryDocumentSnapshots.isEmpty()) return;

                    for (DocumentSnapshot docRelasi : queryDocumentSnapshots) {
                        String idAddon = docRelasi.getString("id_addon");
                        if (idAddon != null) {
                            db.collection("addon").document(idAddon).get()
                                    .addOnSuccessListener(addonDoc -> {
                                        if (addonDoc.exists()) {
                                            AddonModel model = addonDoc.toObject(AddonModel.class);
                                            if (model != null) {
                                                model.setId(addonDoc.getId());
                                                // Hindari duplikasi jika asinkron balik bersamaan
                                                boolean exists = false;
                                                for(AddonModel item : list) {
                                                    if(item.getId().equals(model.getId())) {
                                                        exists = true; 
                                                        break;
                                                    }
                                                }
                                                if(!exists) {
                                                    list.add(model);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (menuId != null) {
            loadAddonData();
        }
    }
}