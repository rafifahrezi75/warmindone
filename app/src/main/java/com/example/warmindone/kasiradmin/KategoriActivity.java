package com.example.warmindone.kasiradmin;

import android.os.Bundle;
import android.content.Intent;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;
import com.example.warmindone.kategori.KategoriModel;
import com.example.warmindone.kategori.KategoriAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class KategoriActivity extends AppCompatActivity {

    private RecyclerView rvKategori;
    private KategoriAdapter adapter;
    private List<KategoriModel> list;
    private FirebaseFirestore db;
    private MaterialButton btnTambah;
    private ImageView btnBack2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori);

        // INIT VIEW
        rvKategori = findViewById(R.id.recyclerKategori);
        btnTambah = findViewById(R.id.btnTambahKategori);
        btnBack2 = findViewById(R.id.btnBack2);

        rvKategori.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new KategoriAdapter(this, list);
        rvKategori.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // CLICK BUTTON
        btnTambah.setOnClickListener(v -> {
            Intent intent = new Intent(KategoriActivity.this, TambahKategoriActivity.class);
            startActivity(intent);
        });

        btnBack2.setOnClickListener(v -> finish());



        loadData();
    }

    private void loadData() {
        db.collection("kategori")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    list.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        KategoriModel k = new KategoriModel();

                        k.setId(doc.getId());
                        k.setKategori(doc.getString("kategori"));

                        list.add(k);
                    }

                    adapter.notifyDataSetChanged();
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}