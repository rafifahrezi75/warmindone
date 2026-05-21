package com.example.warmindone.kasiradmin;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendapatan);

        recyclerPendapatan = findViewById(R.id.recyclerPendapatan);
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
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            list.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                PendapatanModel model = document.toObject(PendapatanModel.class);

                                list.add(model);
                            }

                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}