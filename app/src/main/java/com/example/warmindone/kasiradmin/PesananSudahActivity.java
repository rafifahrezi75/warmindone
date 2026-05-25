package com.example.warmindone.kasiradmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;
import com.example.warmindone.pesanansudah.PesananSudahAdapter;
import com.example.warmindone.pesanansudah.PesananSudahModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PesananSudahActivity extends AppCompatActivity {

    private RecyclerView recyclerPesanan;
    private List<PesananSudahModel> list;
    private PesananSudahAdapter adapter;
    private FirebaseFirestore db;
    private TextView tvDiproses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanansudah);

        // FIX ID: recyclerPesananSelesai sesuai activity_pesanansudah.xml
        recyclerPesanan = findViewById(R.id.recyclerPesananSelesai);
        tvDiproses = findViewById(R.id.tvDiproses);
        ImageView btnBack = findViewById(R.id.btnBack);

        db = FirebaseFirestore.getInstance();

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // Navigasi balik ke Diproses
        if (tvDiproses != null) {
            tvDiproses.setOnClickListener(v -> {
                startActivity(new Intent(this, PesananBelumActivity.class));
                finish();
                overridePendingTransition(0, 0);
            });
        }

        recyclerPesanan.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new PesananSudahAdapter(this, list);
        recyclerPesanan.setAdapter(adapter);

        loadPesananSelesai();
    }

    private void loadPesananSelesai() {
        db.collection("orders")
                .whereEqualTo("status", "selesai")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    list.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            // PEMETAAN MANUAL AGAR TIDAK CRASH (Hindari toObject)
                            PesananSudahModel model = new PesananSudahModel();
                            model.setId_order(doc.getId());
                            model.setId_user(doc.getString("id_user"));
                            model.setMetode(doc.getString("metode"));
                            model.setStatus(doc.getString("status"));
                            model.setTanggal_order(doc.getTimestamp("tanggal_order"));

                            // PROTEKSI HARGA (Handle Angka atau Teks di DB)
                            Object hargaObj = doc.get("total_harga");
                            if (hargaObj == null) hargaObj = doc.get("total_hargs");

                            if (hargaObj != null) {
                                if (hargaObj instanceof Number) {
                                    model.setTotal_harga(((Number) hargaObj).longValue());
                                } else if (hargaObj instanceof String) {
                                    model.setTotal_harga(Long.parseLong((String) hargaObj));
                                }
                            }
                            list.add(model);
                        } catch (Exception e) {
                            Log.e("ERROR_DATA", "Gagal load pesanan: " + doc.getId());
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("FIREBASE", e.getMessage()));
    }
}
