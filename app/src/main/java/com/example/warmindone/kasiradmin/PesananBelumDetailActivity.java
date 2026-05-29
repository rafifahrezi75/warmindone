package com.example.warmindone.kasiradmin;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;
import com.example.warmindone.addondetail.AddonDetailModel;
import com.example.warmindone.pesananbelumdetail.PesananBelumDetailAdapter;
import com.example.warmindone.pesananbelumdetail.PesananBelumDetailModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PesananBelumDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerDetailPesanan;

    private TextInputEditText etNamaPelanggan;
    private TextInputEditText etNominal;
    private MaterialButton btnBayar;

    private FirebaseFirestore db;

    private ArrayList<PesananBelumDetailModel> list;
    private PesananBelumDetailAdapter adapter;

    private String idOrder;
    private long totalHarga = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesananbelumdetail);

        db = FirebaseFirestore.getInstance();

        recyclerDetailPesanan =
                findViewById(R.id.recyclerDetailPesanan);

        etNamaPelanggan =
                findViewById(R.id.etNamaPelanggan);

        etNominal =
                findViewById(R.id.etNominal);

        btnBayar =
                findViewById(R.id.btnBayar);

        list = new ArrayList<>();

        adapter =
                new PesananBelumDetailAdapter(list);

        recyclerDetailPesanan.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerDetailPesanan.setAdapter(adapter);

        idOrder =
                getIntent().getStringExtra("id_order");

        loadDetailPesanan(idOrder);

        // Ambil total harga order
        db.collection("orders")
                .document(idOrder)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    Object totalObj =
                            doc.get("total_harga");

                    if (totalObj instanceof Long) {

                        totalHarga =
                                (Long) totalObj;

                    } else if (totalObj instanceof String) {

                        totalHarga =
                                Long.parseLong(
                                        (String) totalObj
                                );
                    }
                });

        btnBayar.setOnClickListener(v -> {

            String nominalStr =
                    etNominal.getText()
                            .toString()
                            .trim();

            if (nominalStr.isEmpty()) {

                etNominal.setError(
                        "Masukkan nominal uang"
                );

                return;
            }

            long nominal =
                    Long.parseLong(nominalStr);

            if (nominal < totalHarga) {

                new AlertDialog.Builder(this)
                        .setTitle("Pembayaran Gagal")
                        .setMessage(
                                "Uang pelanggan kurang"
                        )
                        .setPositiveButton(
                                "OK",
                                null
                        )
                        .show();

                return;
            }

            long kembalian =
                    nominal - totalHarga;

            db.collection("orders")
                    .document(idOrder)
                    .update(
                            "status", "selesai",
                            "bayar", nominal,
                            "kembali", kembalian
                    )
                    .addOnSuccessListener(unused -> {

                        new AlertDialog.Builder(this)
                                .setTitle(
                                        "Pembayaran Berhasil"
                                )
                                .setMessage(
                                        "Total : Rp " + totalHarga +
                                                "\nBayar : Rp " + nominal +
                                                "\nKembalian : Rp " + kembalian
                                )
                                .setPositiveButton(
                                        "OK",
                                        (dialog, which) -> finish()
                                )
                                .show();
                    })
                    .addOnFailureListener(e -> {

                        new AlertDialog.Builder(this)
                                .setTitle("Error")
                                .setMessage(
                                        e.getMessage()
                                )
                                .setPositiveButton(
                                        "OK",
                                        null
                                )
                                .show();
                    });
        });
    }

    private void loadDetailPesanan(String idOrder) {

        db.collection("orders_detail")
                .whereEqualTo("id_order", idOrder)
                .get()
                .addOnSuccessListener(query -> {

                    list.clear();

                    for (DocumentSnapshot doc :
                            query.getDocuments()) {

                        String idMenu =
                                doc.getString("id_menu");

                        int jumlah =
                                doc.getLong("jumlah")
                                        .intValue();

                        long subtotal =
                                doc.getLong("subtotal");

                        db.collection("menu")
                                .document(idMenu)
                                .get()
                                .addOnSuccessListener(menuDoc -> {

                                    String namaMenu =
                                            menuDoc.getString(
                                                    "nama_menu"
                                            );

                                    ArrayList<AddonDetailModel> addons =
                                            new ArrayList<>();

                                    PesananBelumDetailModel model =
                                            new PesananBelumDetailModel(
                                                    namaMenu,
                                                    jumlah,
                                                    subtotal,
                                                    addons
                                            );

                                    list.add(model);

                                    adapter.notifyDataSetChanged();

                                    db.collection("order_detail_addon")
                                            .whereEqualTo(
                                                    "id_detail",
                                                    doc.getId()
                                            )
                                            .get()
                                            .addOnSuccessListener(addonQuery -> {

                                                for (DocumentSnapshot addonDoc :
                                                        addonQuery.getDocuments()) {

                                                    String idAddon =
                                                            addonDoc.getString(
                                                                    "id_addon"
                                                            );

                                                    int qty =
                                                            addonDoc.getLong(
                                                                    "jumlah"
                                                            ).intValue();

                                                    db.collection("addon")
                                                            .document(idAddon)
                                                            .get()
                                                            .addOnSuccessListener(addonData -> {

                                                                String namaAddon =
                                                                        addonData.getString(
                                                                                "nama_addon"
                                                                        );

                                                                addons.add(
                                                                        new AddonDetailModel(
                                                                                namaAddon,
                                                                                qty
                                                                        )
                                                                );

                                                                adapter.notifyDataSetChanged();
                                                            });
                                                }
                                            });
                                });
                    }
                });
    }
}