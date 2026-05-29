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
import com.example.warmindone.pesananbelum.PesananBelumAdapter;
import com.example.warmindone.pesananbelum.PesananBelumModel;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PesananBelumActivity extends AppCompatActivity {

    private RecyclerView recyclerPesananBelum;
    private PesananBelumAdapter adapter;
    private List<PesananBelumModel> list;
    private FirebaseFirestore db;
    private TextView tvSelesai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesananbelum);

        recyclerPesananBelum = findViewById(R.id.recyclerPesananBelum);
        tvSelesai = findViewById(R.id.tvSelesai);
        ImageView btnBack = findViewById(R.id.btnBack);

        db = FirebaseFirestore.getInstance();

        btnBack.setOnClickListener(v -> finish());

        tvSelesai.setOnClickListener(v -> {
            startActivity(new Intent(this, PesananSudahActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });

        recyclerPesananBelum.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new PesananBelumAdapter(this, list);
        recyclerPesananBelum.setAdapter(adapter);

        loadPesananBelum();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPesananBelum();
    }

    private void loadPesananBelum() {
        db.collection("orders")
                .whereEqualTo("status", "diproses")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    list.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            PesananBelumModel model = new PesananBelumModel();
                            model.setId_order(doc.getId());
                            model.setId_user(doc.getString("id_user"));
                            model.setStatus(doc.getString("status"));
                            model.setMetode(doc.getString("metode"));
                            model.setTanggal_order(doc.getTimestamp("tanggal_order"));

                            // PROTEKSI HARGA (Handle String atau Number)
                            Object hargaObj = doc.get("total_harga");
                            if (hargaObj == null) hargaObj = doc.get("total_hargs"); // Handle typo

                            if (hargaObj != null) {
                                if (hargaObj instanceof String) {
                                    model.setTotal_harga(Long.parseLong((String) hargaObj));
                                } else {
                                    model.setTotal_harga(doc.getLong(doc.contains("total_harga") ? "total_harga" : "total_hargs"));
                                }
                            }

                            list.add(model);
                        } catch (Exception e) {
                            Log.e("PARSING_ERROR", "Gagal load data: " + doc.getId());
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
