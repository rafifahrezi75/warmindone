package com.example.warmindone.pelanggan;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;
import com.example.warmindone.history.HistoryAdapter;
import com.example.warmindone.history.HistoryAddonModel;
import com.example.warmindone.history.HistoryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvPesanan;
    private LinearLayout layoutEmptyPesanan;

    private FirebaseFirestore db;

    private ArrayList<HistoryModel> list;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(
            @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db = FirebaseFirestore.getInstance();

        rvPesanan =
                findViewById(
                        R.id.rvPesanan
                );

        layoutEmptyPesanan =
                findViewById(
                        R.id.layoutEmptyPesanan
                );

        list = new ArrayList<>();

        adapter =
                new HistoryAdapter(
                        this,
                        list
                );

        rvPesanan.setLayoutManager(
                new LinearLayoutManager(
                        this
                )
        );

        rvPesanan.setAdapter(
                adapter
        );

        loadHistory();
    }

    private void loadHistory() {

        String userId =
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser()
                        .getUid();

        db.collection("orders")
                .whereEqualTo(
                        "id_user",
                        userId
                )
                .whereEqualTo(
                        "status",
                        "selesai"
                )
                .get()
                .addOnSuccessListener(query -> {

                    list.clear();

                    if(query.isEmpty()) {

                        layoutEmptyPesanan
                                .setVisibility(
                                        View.VISIBLE
                                );

                        rvPesanan
                                .setVisibility(
                                        View.GONE
                                );

                        return;
                    }

                    layoutEmptyPesanan
                            .setVisibility(
                                    View.GONE
                            );

                    rvPesanan
                            .setVisibility(
                                    View.VISIBLE
                            );

                    for(DocumentSnapshot orderDoc :
                            query.getDocuments()) {

                        HistoryModel model =
                                new HistoryModel();

                        model.setIdOrder(
                                orderDoc.getId()
                        );

                        model.setStatus(
                                orderDoc.getString(
                                        "status"
                                )
                        );

                        model.setTanggalOrder(
                                orderDoc.getTimestamp(
                                        "tanggal_order"
                                )
                        );

                        Long totalHarga =
                                orderDoc.getLong(
                                        "total_harga"
                                );

                        if(totalHarga == null) {
                            totalHarga = 0L;
                        }

                        model.setTotalHarga(
                                totalHarga
                        );

                        loadMenuPertama(
                                orderDoc.getId(),
                                model
                        );
                    }
                });
    }

    private void loadMenuPertama(
            String idOrder,
            HistoryModel model) {

        db.collection("orders_detail")
                .whereEqualTo(
                        "id_order",
                        idOrder
                )
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {

                    if(query.isEmpty()) {

                        list.add(model);

                        adapter.notifyDataSetChanged();

                        return;
                    }

                    DocumentSnapshot detailDoc =
                            query.getDocuments()
                                    .get(0);

                    model.setJumlah(
                            detailDoc.getLong(
                                    "jumlah"
                            )
                    );

                    String idMenu =
                            detailDoc.getString(
                                    "id_menu"
                            );

                    db.collection("menu")
                            .document(idMenu)
                            .get()
                            .addOnSuccessListener(menuDoc -> {

                                model.setNamaMenu(
                                        menuDoc.getString(
                                                "nama_menu"
                                        )
                                );

                                loadAddon(
                                        detailDoc.getId(),
                                        model
                                );
                            });
                });
    }

    private void loadAddon(
            String idDetail,
            HistoryModel model) {

        ArrayList<HistoryAddonModel>
                addonList =
                new ArrayList<>();

        db.collection(
                        "order_detail_addon"
                )
                .whereEqualTo(
                        "id_detail",
                        idDetail
                )
                .get()
                .addOnSuccessListener(query -> {

                    if(query.isEmpty()) {

                        model.setAddons(
                                addonList
                        );

                        list.add(model);

                        adapter.notifyDataSetChanged();

                        return;
                    }

                    for(DocumentSnapshot addonDoc :
                            query.getDocuments()) {

                        String idAddon =
                                addonDoc.getString(
                                        "id_addon"
                                );

                        Long jumlah =
                                addonDoc.getLong(
                                        "jumlah"
                                );

                        db.collection("addon")
                                .document(idAddon)
                                .get()
                                .addOnSuccessListener(doc -> {

                                    HistoryAddonModel addon =
                                            new HistoryAddonModel();

                                    addon.setJumlah(
                                            jumlah
                                    );

                                    addon.setNamaAddon(
                                            doc.getString(
                                                    "nama_addon"
                                            )
                                    );

                                    addonList.add(
                                            addon
                                    );

                                    model.setAddons(
                                            addonList
                                    );

                                    if(!list.contains(model)) {

                                        list.add(
                                                model
                                        );
                                    }

                                    adapter.notifyDataSetChanged();
                                });
                    }
                });
    }
}