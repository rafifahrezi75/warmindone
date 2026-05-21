package com.example.warmindone.kasiradmin;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.warmindone.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TambahMenuActivity extends AppCompatActivity {

    private ImageView imgPreview, btnBack;

    private EditText etNamaMenu,
            etDeskripsi,
            etHarga,
            etStok,
            etStatus;

    private Spinner spKategori;

    private MaterialButton btnPilihFoto,
            btnTambah;

    private Uri imageUri;

    private FirebaseFirestore db;

    // LIST KATEGORI
    private final List<String> kategoriList = new ArrayList<>();
    private final List<String> kategoriIdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambahmenu);

        // INIT VIEW
        imgPreview = findViewById(R.id.imgPreview);
        btnBack = findViewById(R.id.btnBack);

        etNamaMenu = findViewById(R.id.etNamaMenu);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        etHarga = findViewById(R.id.etHarga);
        etStok = findViewById(R.id.etStok);
        etStatus = findViewById(R.id.etStatus);

        spKategori = findViewById(R.id.spKategori);

        btnPilihFoto = findViewById(R.id.btnPilihFoto);
        btnTambah = findViewById(R.id.btnTambah);

        db = FirebaseFirestore.getInstance();

        // BACK
        btnBack.setOnClickListener(v -> finish());

        // LOAD KATEGORI
        loadKategori();

        // CLOUDINARY CONFIG
        Map<String, String> config = new HashMap<>();

        config.put("cloud_name", "dl1lswlpb");
        config.put("api_key", "498637479124293");
        config.put("api_secret", "HXNbvKr8WFmjLzrYyiQG4IPILnM");

        try {

            MediaManager.init(this, config);

        } catch (Exception e) {

        }

        // PILIH FOTO
        btnPilihFoto.setOnClickListener(v -> {
            pilihImage.launch("image/*");
        });

        // TAMBAH MENU
        btnTambah.setOnClickListener(v -> {

            if (validateInput()) {

                uploadImage();
            }
        });
    }

    // LOAD DATA KATEGORI
    private void loadKategori() {

        kategoriList.clear();
        kategoriIdList.clear();

        db.collection("kategori")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {

                        kategoriIdList.add(doc.getId());

                        String namaKategori =
                                doc.getString("kategori");

                        kategoriList.add(namaKategori);
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(
                                    this,
                                    android.R.layout.simple_spinner_item,
                                    kategoriList
                            );

                    adapter.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item
                    );

                    spKategori.setAdapter(adapter);
                });
    }

    // VALIDASI
    private boolean validateInput() {

        if (imageUri == null) {

            Toast.makeText(
                    this,
                    "Pilih foto dulu",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        if (etNamaMenu.getText().toString().trim().isEmpty()) {

            etNamaMenu.setError("Nama menu wajib diisi");

            return false;
        }

        if (etHarga.getText().toString().trim().isEmpty()) {

            etHarga.setError("Harga wajib diisi");

            return false;
        }

        if (etStok.getText().toString().trim().isEmpty()) {

            etStok.setError("Stok wajib diisi");

            return false;
        }

        return true;
    }

    // PILIH FOTO
    ActivityResultLauncher<String> pilihImage =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    result -> {

                        if (result != null) {

                            imageUri = result;

                            imgPreview.setImageURI(result);
                        }
                    });

    // UPLOAD IMAGE
    private void uploadImage() {

        Toast.makeText(
                this,
                "Uploading...",
                Toast.LENGTH_SHORT
        ).show();

        MediaManager.get().upload(imageUri)

                .callback(new UploadCallback() {

                    @Override
                    public void onStart(String requestId) {

                    }

                    @Override
                    public void onProgress(String requestId,
                                           long bytes,
                                           long totalBytes) {

                    }

                    @Override
                    public void onSuccess(String requestId,
                                          Map resultData) {

                        String imageUrl =
                                resultData.get("secure_url")
                                        .toString();

                        saveToFirestore(imageUrl);
                    }

                    @Override
                    public void onError(String requestId,
                                        ErrorInfo error) {

                        Toast.makeText(
                                TambahMenuActivity.this,
                                "Upload gagal : "
                                        + error.getDescription(),
                                Toast.LENGTH_LONG
                        ).show();
                    }

                    @Override
                    public void onReschedule(String requestId,
                                             ErrorInfo error) {

                    }
                })

                .dispatch();
    }

    // SIMPAN FIRESTORE
    private void saveToFirestore(String imageUrl) {

        Map<String, Object> map =
                new HashMap<>();

        int selectedPosition =
                spKategori.getSelectedItemPosition();

        String selectedKategoriId =
                kategoriIdList.get(selectedPosition);

        map.put(
                "nama_menu",
                etNamaMenu.getText()
                        .toString()
                        .trim()
        );

        map.put(
                "deskripsi",
                etDeskripsi.getText()
                        .toString()
                        .trim()
        );

        map.put(
                "harga",
                Integer.parseInt(
                        etHarga.getText()
                                .toString()
                                .trim()
                )
        );

        map.put(
                "stok",
                Integer.parseInt(
                        etStok.getText()
                                .toString()
                                .trim()
                )
        );

        map.put(
                "id_kategori",
                selectedKategoriId
        );

        map.put(
                "status",
                etStatus.getText()
                        .toString()
                        .trim()
        );

        map.put(
                "foto",
                imageUrl
        );

        db.collection("menu")

                .add(map)

                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            this,
                            "Menu berhasil ditambahkan",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }
}