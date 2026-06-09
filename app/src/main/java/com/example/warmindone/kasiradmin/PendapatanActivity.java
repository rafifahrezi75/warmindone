package com.example.warmindone.kasiradmin;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.example.warmindone.R;
import com.example.warmindone.pendapatan.PendapatanAdapter;
import com.example.warmindone.pendapatan.PendapatanModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PendapatanActivity extends AppCompatActivity {

    private RecyclerView recyclerPendapatan;
    private PendapatanAdapter adapter;
    private List<PendapatanModel> list;
    private TextView tvTotalPendapatan;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendapatan);

        recyclerPendapatan = findViewById(R.id.recyclerPendapatan);
        tvTotalPendapatan = findViewById(R.id.tvTotalPendapatan);
        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        recyclerPendapatan.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new PendapatanAdapter(this, list);

        recyclerPendapatan.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadDataPendapatan();
    }

    private void loadDataPendapatan() {

        db.collection("pendapatan")
                .addSnapshotListener((value, error) -> {

                    if (error != null || value == null) {
                        return;
                    }

                    list.clear();

                    long totalSemuaPendapatan = 0;

                    for (QueryDocumentSnapshot document : value) {

                        PendapatanModel model =
                                document.toObject(PendapatanModel.class);

                        list.add(model);

                        totalSemuaPendapatan += model.getTotal();
                    }

                    adapter.notifyDataSetChanged();

                    tvTotalPendapatan.setText(
                            "Rp " +
                                    String.format("%,d", totalSemuaPendapatan)
                                            .replace(',', '.')
                    );
                });
    }
}