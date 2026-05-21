package com.example.warmindone.kasiradmin;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;
import com.example.warmindone.pesananselesai.PesananSelesaiAdapter;
import com.example.warmindone.pesananselesai.PesananSelesaiModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PesananSelesaiActivity extends AppCompatActivity {

    private RecyclerView recyclerPendapatan;
    private PesananSelesaiAdapter adapter;
    private List<PesananSelesaiModel> list;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_pesananselesai);

        recyclerPendapatan =
                findViewById(R.id.recyclerPesananSelesai);

        ImageView btnBack =
                findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        recyclerPendapatan.setLayoutManager(
                new LinearLayoutManager(this)
        );

        list = new ArrayList<>();

        adapter = new PesananSelesaiAdapter(this, list);

        recyclerPendapatan.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadPesananSelesai();
    }

    private void loadPesananSelesai() {

        db.collection("orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    list.clear();

                    for (QueryDocumentSnapshot document :
                            queryDocumentSnapshots) {

                        String status =
                                document.getString("status");

                        if (status != null &&
                                status.equals("selesai")) {

                            PesananSelesaiModel model =
                                    document.toObject(
                                            PesananSelesaiModel.class
                                    );

                            model.setId_order(document.getId());

                            list.add(model);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    Log.d("DATA_FIREBASE",
                            "Jumlah data : " + list.size());
                })

                .addOnFailureListener(e -> {

                    Log.e("DATA_FIREBASE",
                            e.getMessage());
                });
    }
}