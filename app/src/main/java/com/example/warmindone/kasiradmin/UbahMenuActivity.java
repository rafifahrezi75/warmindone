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

import com.bumptech.glide.Glide;
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

public class UbahMenuActivity extends AppCompatActivity {

    private ImageView imgPreview, btnBack;

    private EditText etNamaMenu,
            etDeskripsi,
            etHarga,
            etStok,
            etStatus;

    private Spinner spKategori;

    private MaterialButton btnPilihFoto,
            btnSimpan;

    private Uri imageUri;

    private FirebaseFirestore db;

    private String documentId;
    private String oldImageUrl = "";

    private List<String> kategoriIdList = new ArrayList<>();
    private List<String> kategoriNamaList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubahmenu);

        // INIT
        imgPreview = findViewById(R.id.imgPreview);
        btnBack = findViewById(R.id.btnBack);

        etNamaMenu = findViewById(R.id.etNamaMenu);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        etHarga = findViewById(R.id.etHarga);
        etStok = findViewById(R.id.etStok);
        etStatus = findViewById(R.id.etStatus);

        spKategori = findViewById(R.id.spKategori);

        btnPilihFoto = findViewById(R.id.btnPilihFoto);
        btnSimpan = findViewById(R.id.btnSimpan);

        db = FirebaseFirestore.getInstance();

        documentId = getIntent().getStringExtra("id");

        // BACK
        btnBack.setOnClickListener(v -> finish());

        // CLOUDINARY
        Map config = new HashMap();

        config.put("cloud_name", "dl1lswlpb");
        config.put("api_key", "498637479124293");
        config.put("api_secret", "HXNbvKr8WFmjLzrYyiQG4IPILnM");

        try {
            MediaManager.init(this, config);
        } catch (Exception e) {

        }

        // LOAD KATEGORI
        loadKategori();

        // LOAD DATA MENU
        loadDataMenu();

        // PILIH FOTO
        btnPilihFoto.setOnClickListener(v -> {
            pilihImage.launch("image/*");
        });

        // SIMPAN
        btnSimpan.setOnClickListener(v -> {

            if (validateInput()) {

                if (imageUri != null) {

                    uploadImage();

                } else {

                    updateFirestore(oldImageUrl);
                }
            }
        });
    }

    // VALIDASI
    private boolean validateInput() {

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

    // LOAD KATEGORI
    private void loadKategori() {

        db.collection("kategori")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    kategoriIdList.clear();
                    kategoriNamaList.clear();

                    for (DocumentSnapshot doc :
                            queryDocumentSnapshots) {

                        kategoriIdList.add(doc.getId());

                        kategoriNamaList.add(
                                doc.getString("kategori")
                        );
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(
                                    this,
                                    android.R.layout.simple_spinner_item,
                                    kategoriNamaList
                            );

                    adapter.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item
                    );

                    spKategori.setAdapter(adapter);
                });
    }

    // LOAD DATA MENU
    private void loadDataMenu() {

        db.collection("menu")
                .document(documentId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (doc.exists()) {

                        etNamaMenu.setText(
                                doc.getString("nama_menu")
                        );

                        etDeskripsi.setText(
                                doc.getString("deskripsi")
                        );

                        etHarga.setText(
                                String.valueOf(
                                        doc.getLong("harga")
                                )
                        );

                        etStok.setText(
                                String.valueOf(
                                        doc.getLong("stok")
                                )
                        );

                        etStatus.setText(
                                doc.getString("status")
                        );

                        oldImageUrl =
                                doc.getString("foto");

                        Glide.with(this)
                                .load(oldImageUrl)
                                .into(imgPreview);

                        String kategoriId =
                                doc.getString("id_kategori");

                        spKategori.post(() -> {

                            for (int i = 0;
                                 i < kategoriIdList.size();
                                 i++) {

                                if (kategoriIdList.get(i)
                                        .equals(kategoriId)) {

                                    spKategori.setSelection(i);

                                    break;
                                }
                            }
                        });
                    }
                });
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

    // UPLOAD FOTO
    private void uploadImage() {

        Toast.makeText(this,
                "Uploading...",
                Toast.LENGTH_SHORT).show();

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

                        updateFirestore(imageUrl);
                    }

                    @Override
                    public void onError(String requestId,
                                        ErrorInfo error) {

                        Toast.makeText(
                                UbahMenuActivity.this,
                                error.getDescription(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    @Override
                    public void onReschedule(String requestId,
                                             ErrorInfo error) {

                    }
                })

                .dispatch();
    }

    // UPDATE FIRESTORE
    private void updateFirestore(String imageUrl) {

        Map<String, Object> map =
                new HashMap<>();

        map.put("nama_menu",
                etNamaMenu.getText()
                        .toString()
                        .trim());

        map.put("deskripsi",
                etDeskripsi.getText()
                        .toString()
                        .trim());

        map.put("harga",
                Integer.parseInt(
                        etHarga.getText()
                                .toString()
                                .trim()
                ));

        map.put("stok",
                Integer.parseInt(
                        etStok.getText()
                                .toString()
                                .trim()
                ));

        map.put("status",
                etStatus.getText()
                        .toString()
                        .trim());

        map.put("foto",
                imageUrl);

        map.put("id_kategori",
                kategoriIdList.get(
                        spKategori.getSelectedItemPosition()
                ));

        db.collection("menu")
                .document(documentId)
                .update(map)

                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Menu berhasil diubah",
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