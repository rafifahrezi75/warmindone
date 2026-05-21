package com.example.warmindone.kasiradmin;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;
import com.example.warmindone.pesananselesai.PesananSelesaiAdapter;
import com.example.warmindone.pesananselesai.PesananSelesaiModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PesananSelesaiActivity extends AppCompatActivity {

    private RecyclerView recyclerPesananSelesai;
    private PesananSelesaiAdapter adapter;
    private List<PesananSelesaiModel> list;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_pesananselesai);

        recyclerPesananSelesai = findViewById(R.id.recyclerPesananSelesai);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        recyclerPesananSelesai.setLayoutManager(
                new LinearLayoutManager(this)
        );

        list = new ArrayList<>();

        adapter = new PesananSelesaiAdapter(this, list);
        recyclerPesananSelesai.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadPesananSelesai();
    }

    private void loadPesananSelesai() {

        db.collection("orders")
                .whereEqualTo("status", "selesai")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    list.clear();

                    for (QueryDocumentSnapshot document :
                            queryDocumentSnapshots) {

                        PesananSelesaiModel model =
                                document.toObject(
                                        PesananSelesaiModel.class
                                );

                        model.setId_order(document.getId());

                        list.add(model);
                    }

                    adapter.notifyDataSetChanged();
                })

                .addOnFailureListener(e ->
                        Log.e("FIREBASE_ERROR",
                                e.getMessage()));
    }
}