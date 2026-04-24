package com.example.warmindone.kasiradmin;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.warmindone.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TambahKategoriActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etNamaKategori;
    private MaterialButton btnTambah;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambahkategori);

        btnBack = findViewById(R.id.btnBack);
        etNamaKategori = findViewById(R.id.etNamaKategori);
        btnTambah = findViewById(R.id.btnTambah);

        db = FirebaseFirestore.getInstance();

        // tombol back
        btnBack.setOnClickListener(v -> onBackPressed());

        // tombol tambah
        btnTambah.setOnClickListener(v -> {
            String namaKategori = etNamaKategori.getText().toString().trim();

            if (namaKategori.isEmpty()) {
                etNamaKategori.setError("Nama kategori tidak boleh kosong");
                return;
            }

            // buat data
            Map<String, Object> data = new HashMap<>();
            data.put("kategori", namaKategori);

            // simpan ke firestore
            db.collection("kategori")
                    .add(data)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Kategori berhasil ditambah", Toast.LENGTH_SHORT).show();
                        finish(); // balik ke halaman sebelumnya
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Gagal menambah kategori", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}