package com.example.warmindone.kasiradmin;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.warmindone.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class UbahKategoriActivity extends AppCompatActivity {

    private EditText etNamaKategori;
    private MaterialButton btnUpdate;
    private ImageView btnBack;

    private FirebaseFirestore db;
    private String id, nama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubahkategori);

        // INIT VIEW
        etNamaKategori = findViewById(R.id.etNamaKategori);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnBack = findViewById(R.id.btnBack);

        db = FirebaseFirestore.getInstance();

        // AMBIL DATA DARI INTENT
        id = getIntent().getStringExtra("id");
        nama = getIntent().getStringExtra("nama");

        // SET KE INPUT
        etNamaKategori.setText(nama);

        // BACK
        btnBack.setOnClickListener(v -> onBackPressed());

        // UPDATE
        btnUpdate.setOnClickListener(v -> {
            String namaBaru = etNamaKategori.getText().toString().trim();

            if (namaBaru.isEmpty()) {
                etNamaKategori.setError("Tidak boleh kosong");
                return;
            }

            db.collection("kategori")
                    .document(id)
                    .update("kategori", namaBaru)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Berhasil update", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Gagal update", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}