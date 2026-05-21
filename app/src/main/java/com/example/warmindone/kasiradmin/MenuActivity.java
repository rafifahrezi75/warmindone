package com.example.warmindone.kasiradmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;
import com.example.warmindone.menu.MenuAdapter;
import com.example.warmindone.menu.MenuModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerMenu;

    private MenuAdapter adapter;

    private List<MenuModel> list;

    private FirebaseFirestore db;

    private MaterialButton btnTambahMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        recyclerMenu = findViewById(R.id.recyclerMenu);

        btnTambahMenu = findViewById(R.id.btnTambahMenu);

        recyclerMenu.setLayoutManager(
                new LinearLayoutManager(this)
        );

        list = new ArrayList<>();

        adapter = new MenuAdapter(this, list);

        recyclerMenu.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        btnTambahMenu.setOnClickListener(v -> {

            Intent intent =
                    new Intent(MenuActivity.this,
                            TambahMenuActivity.class);

            startActivity(intent);
        });
    }

    private void loadData() {

        list.clear();

        adapter.notifyDataSetChanged();

        db.collection("menu")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (DocumentSnapshot doc :
                            queryDocumentSnapshots) {

                        String id = doc.getId();

                        String namaMenu =
                                doc.getString("nama_menu");

                        String kategoriId =
                                doc.getString("id_kategori");

                        String imageUrl =
                                doc.getString("foto");

                        if (kategoriId != null &&
                                !kategoriId.isEmpty()) {

                            db.collection("kategori")
                                    .document(kategoriId)
                                    .get()
                                    .addOnSuccessListener(kategoriDoc -> {

                                        String namaKategori;

                                        if (kategoriDoc.exists()) {

                                            namaKategori =
                                                    kategoriDoc.getString("kategori");

                                        } else {

                                            namaKategori =
                                                    "Tanpa Kategori";
                                        }

                                        MenuModel model =
                                                new MenuModel();

                                        model.setId(id);

                                        model.setNamaMenu(namaMenu);

                                        model.setKategoriId(kategoriId);

                                        model.setNamaKategori(namaKategori);

                                        model.setImageUrl(imageUrl);

                                        list.add(model);

                                        adapter.notifyItemInserted(
                                                list.size() - 1
                                        );
                                    });

                        } else {

                            MenuModel model =
                                    new MenuModel();

                            model.setId(id);

                            model.setNamaMenu(namaMenu);

                            model.setKategoriId("");

                            model.setNamaKategori("Tanpa Kategori");

                            model.setImageUrl(imageUrl);

                            list.add(model);

                            adapter.notifyItemInserted(
                                    list.size() - 1
                            );
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadData();
    }
}