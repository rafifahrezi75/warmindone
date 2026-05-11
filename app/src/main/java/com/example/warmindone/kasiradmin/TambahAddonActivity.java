package com.example.warmindone.kasiradmin;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.warmindone.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TambahAddonActivity extends AppCompatActivity {

    private ImageView imgPreview, btnBack;
    private EditText etNamaAddon, etHargaAddon, etStokAddon;
    private MaterialButton btnPilihFoto, btnSimpan;
    private Uri imageUri;
    private FirebaseFirestore db;
    private String menuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambahaddon);

        imgPreview = findViewById(R.id.imgPreview);
        btnBack = findViewById(R.id.btnBack);
        etNamaAddon = findViewById(R.id.etNamaAddon);
        etHargaAddon = findViewById(R.id.etHargaAddon);
        etStokAddon = findViewById(R.id.etStokAddon);
        btnPilihFoto = findViewById(R.id.btnPilihFoto);
        btnSimpan = findViewById(R.id.btnSimpan);

        db = FirebaseFirestore.getInstance();
        
        // Ambil menuId dari intent
        menuId = getIntent().getStringExtra("menuId");

        // Konfigurasi Cloudinary
        try {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "dl1lswlpb");
            config.put("api_key", "498637479124293");
            config.put("api_secret", "HXNbvKr8WFmjLzrYyiQG4IPILnM");
            MediaManager.init(this, config);
        } catch (Exception e) {}

        btnBack.setOnClickListener(v -> finish());
        btnPilihFoto.setOnClickListener(v -> pilihImage.launch("image/*"));
        btnSimpan.setOnClickListener(v -> {
            if (validateInput()) {
                uploadImage();
            }
        });
    }

    private boolean validateInput() {
        if (imageUri == null) {
            Toast.makeText(this, "Pilih foto dulu", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etNamaAddon.getText().toString().trim().isEmpty() ||
            etHargaAddon.getText().toString().trim().isEmpty() ||
            etStokAddon.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    ActivityResultLauncher<String> pilihImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            result -> {
                if (result != null) {
                    imageUri = result;
                    imgPreview.setImageURI(result);
                }
            });

    private void uploadImage() {
        Toast.makeText(this, "Mengunggah...", Toast.LENGTH_SHORT).show();
        MediaManager.get().upload(imageUri)
                .callback(new UploadCallback() {
                    @Override public void onStart(String requestId) {}
                    @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        saveToFirestore(resultData.get("secure_url").toString());
                    }
                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(TambahAddonActivity.this, "Gagal: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }

    private void saveToFirestore(String imageUrl) {
        Map<String, Object> addon = new HashMap<>();
        addon.put("nama_addon", etNamaAddon.getText().toString().trim());
        addon.put("harga", Integer.parseInt(etHargaAddon.getText().toString().trim()));
        addon.put("stok", Integer.parseInt(etStokAddon.getText().toString().trim()));
        addon.put("fotoaddon", imageUrl);

        // 1. Simpan ke koleksi 'addon'
        db.collection("addon").add(addon)
                .addOnSuccessListener(documentReference -> {
                    String newAddonId = documentReference.getId();
                    
                    // 2. Simpan ke koleksi 'menu_addon' (Tabel Penghubung)
                    if (menuId != null) {
                        Map<String, Object> junction = new HashMap<>();
                        junction.put("id_menu", menuId);
                        junction.put("id_addon", newAddonId);

                        db.collection("menu_addon").add(junction)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Addon berhasil ditambahkan ke menu", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                    } else {
                        Toast.makeText(this, "Addon tersimpan (tanpa menu)", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}