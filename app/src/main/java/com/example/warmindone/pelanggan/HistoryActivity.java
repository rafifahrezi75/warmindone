package com.example.warmindone.pelanggan;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.widget.TextView;
import android.widget.PopupMenu;

import com.example.warmindone.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.example.warmindone.R;
import com.example.warmindone.history.HistoryAdapter;
import com.example.warmindone.history.HistoryAddonModel;
import com.example.warmindone.history.HistoryModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.util.Log;


import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvPesanan;
    private LinearLayout layoutEmptyPesanan;

    private FirebaseFirestore db;

    private ArrayList<HistoryModel> list;
    private HistoryAdapter adapter;
    private TextView tvProfileCircle;

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

        tvProfileCircle =
                findViewById(
                        R.id.tvProfileCircle
                );

        loadProfile();

        tvProfileCircle.setOnClickListener(
                v -> showProfileMenu(v)
        );

        loadHistory();
    }

    private void loadProfile() {

        String uid =
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser()
                        .getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    String nama =
                            doc.getString("nama");

                    if (nama != null &&
                            !nama.isEmpty()) {

                        tvProfileCircle.setText(
                                nama.substring(0, 1)
                                        .toUpperCase()
                        );
                    }
                });
    }

    private void showProfileMenu(View view) {

        PopupMenu popupMenu =
                new PopupMenu(
                        this,
                        view
                );

        popupMenu.getMenu().add(
                "Profile"
        );

        popupMenu.getMenu().add(
                "Logout"
        );

        popupMenu.setOnMenuItemClickListener(item -> {

            if(item.getTitle()
                    .equals("Profile")) {

                Intent intent =
                        new Intent(
                                HistoryActivity.this,
                                ProfileActivity.class
                        );

                startActivity(intent);

            } else if(item.getTitle()
                    .equals("Logout")) {

                FirebaseAuth
                        .getInstance()
                        .signOut();

                Intent intent =
                        new Intent(
                                HistoryActivity.this,
                                LoginActivity.class
                        );

                intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                );

                startActivity(intent);

                finish();
            }

            return true;
        });

        popupMenu.show();
    }

    private void loadHistory() {

        FirebaseUser user =
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser();

        Log.d("HISTORY", "User = " + user);

        if (user == null) {
            Log.e("HISTORY", "User belum login!");
            return;
        }

        String userId = user.getUid();

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

                        Object hargaObj = orderDoc.get("total_harga");

                        long totalHarga = 0;

                        if (hargaObj instanceof Long) {
                            totalHarga = (Long) hargaObj;
                        } else if (hargaObj instanceof Integer) {
                            totalHarga = ((Integer) hargaObj).longValue();
                        } else if (hargaObj instanceof String) {
                            try {
                                totalHarga = Long.parseLong((String) hargaObj);
                            } catch (Exception e) {
                                totalHarga = 0;
                            }
                        }

                        model.setTotalHarga(totalHarga);

                        loadMenuPertama(
                                orderDoc.getId(),
                                model
                        );
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("HISTORY", "Query orders gagal", e);
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

                    Long jumlah = detailDoc.getLong("jumlah");

                    if (jumlah == null) {
                        jumlah = 0L;
                    }

                    model.setJumlah(jumlah);

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