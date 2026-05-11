package com.example.warmindone.kasiradmin;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.warmindone.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UbahAddonActivity extends AppCompatActivity {

    private ImageView imgPreview, btnBack;
    private EditText etNamaAddon, etHargaAddon, etStokAddon;
    private MaterialButton btnPilihFoto, btnUpdate;
    private Uri imageUri;
    private FirebaseFirestore db;
    private String addonId, currentImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubahaddon);

        imgPreview = findViewById(R.id.imgPreview);
        btnBack = findViewById(R.id.btnBack);
        etNamaAddon = findViewById(R.id.etNamaAddon);
        etHargaAddon = findViewById(R.id.etHargaAddon);
        etStokAddon = findViewById(R.id.etStokAddon);
        btnPilihFoto = findViewById(R.id.btnPilihFoto);
        btnUpdate = findViewById(R.id.btnUpdate);

        db = FirebaseFirestore.getInstance();
        addonId = getIntent().getStringExtra("id");

        btnBack.setOnClickListener(v -> finish());

        // Konfigurasi Cloudinary (Pastikan kredensial benar)
        try {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "dl1lswlpb");
            config.put("api_key", "498637479124293");
            config.put("api_secret", "HXNbvKr8WFmjLzrYyiQG4IPILnM");
            MediaManager.init(this, config);
        } catch (Exception e) {}

        if (addonId != null) {
            loadAddonData();
        }

        btnPilihFoto.setOnClickListener(v -> pilihImage.launch("image/*"));

        btnUpdate.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImage();
            } else {
                updateFirestore(currentImageUrl);
            }
        });
    }

    private void loadAddonData() {
        db.collection("addon").document(addonId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        // SESUAIKAN DENGAN NAMA FIELD DATABASE
                        etNamaAddon.setText(doc.getString("nama_addon"));
                        etHargaAddon.setText(String.valueOf(doc.getLong("harga")));
                        etStokAddon.setText(String.valueOf(doc.getLong("stok")));
                        currentImageUrl = doc.getString("fotoaddon");

                        Glide.with(this)
                                .load(currentImageUrl)
                                .placeholder(R.drawable.ic_menu)
                                .into(imgPreview);
                    }
                });
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
        Toast.makeText(this, "Updating Image...", Toast.LENGTH_SHORT).show();
        MediaManager.get().upload(imageUri)
                .callback(new UploadCallback() {
                    @Override public void onStart(String requestId) {}
                    @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        updateFirestore(resultData.get("secure_url").toString());
                    }
                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(UbahAddonActivity.this, "Upload Gagal: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }

    private void updateFirestore(String imageUrl) {
        Map<String, Object> map = new HashMap<>();
        // PERBAIKAN: Gunakan nama field yang sama dengan Firestore
        map.put("nama_addon", etNamaAddon.getText().toString().trim());
        map.put("harga", Integer.parseInt(etHargaAddon.getText().toString().trim()));
        map.put("stok", Integer.parseInt(etStokAddon.getText().toString().trim()));
        map.put("fotoaddon", imageUrl);

        db.collection("addon").document(addonId).update(map)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Addon berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}