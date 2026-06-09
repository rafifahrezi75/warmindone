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

import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Locale;

import java.util.ArrayList;

public class PesananBelumDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerDetailPesanan;

    private TextInputEditText etNamaPelanggan;
    private TextInputEditText etNominal;
    private MaterialButton btnBayar;
    private TextView tvInvoice;
    private TextView tvTanggal;
    private TextView tvStatus;
    private TextView tvTotal;

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

        tvInvoice = findViewById(R.id.tvInvoice);
        tvTanggal = findViewById(R.id.tvTanggal);
        tvStatus = findViewById(R.id.tvStatus);
        tvTotal = findViewById(R.id.tvTotal);

        list = new ArrayList<>();

        adapter =
                new PesananBelumDetailAdapter(list);

        recyclerDetailPesanan.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerDetailPesanan.setAdapter(adapter);

        idOrder =
                getIntent().getStringExtra("id_order");

        android.util.Log.d("DETAIL_ORDER", "ID ORDER = " + idOrder);

        loadDetailPesanan(idOrder);

        // Ambil total harga order
        db.collection("orders")
                .document(idOrder)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    // Invoice
                    String inv = idOrder.length() > 7
                            ? idOrder.substring(0, 7)
                            : idOrder;

                    tvInvoice.setText("INV-" + inv);

                    // Status
                    tvStatus.setText(doc.getString("status"));

                    // Tanggal
                    if (doc.getTimestamp("tanggal_order") != null) {

                        SimpleDateFormat sdf =
                                new SimpleDateFormat(
                                        "dd MMM yyyy HH:mm",
                                        new Locale("id", "ID")
                                );

                        tvTanggal.setText(
                                sdf.format(
                                        doc.getTimestamp("tanggal_order")
                                                .toDate()
                                )
                        );
                    }

                    // Total
                    Object totalObj = doc.get("total_harga");

                    if (totalObj instanceof Long) {

                        totalHarga = (Long) totalObj;

                    } else if (totalObj instanceof String) {

                        totalHarga =
                                Long.parseLong((String) totalObj);
                    }

                    tvTotal.setText(
                            "Rp " +
                                    String.format("%,d", totalHarga)
                                            .replace(',', '.')
                    );

                    // Nama pelanggan
                    String userId = doc.getString("id_user");

                    if (userId != null) {

                        db.collection("users")
                                .document(userId)
                                .get()
                                .addOnSuccessListener(userDoc -> {

                                    String nama =
                                            userDoc.getString("nama");

                                    etNamaPelanggan.setText(
                                            nama != null
                                                    ? nama
                                                    : "-"
                                    );
                                });
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

        list.clear();
        adapter.notifyDataSetChanged();

        db.collection("orders_detail")
                .whereEqualTo("id_order", idOrder)
                .get()
                .addOnSuccessListener(query -> {

                    android.util.Log.d(
                            "DETAIL_ORDER",
                            "Cari detail untuk order = "
                                    + idOrder
                                    + ", ditemukan = "
                                    + query.size()
                    );

                    for (DocumentSnapshot doc : query.getDocuments()) {

                        android.util.Log.d(
                                "DETAIL_ORDER",
                                "orders_detail id = "
                                        + doc.getId()
                                        + ", data = "
                                        + doc.getData()
                        );

                        String idMenu = doc.getString("id_menu");

                        Long jumlahLong = doc.getLong("jumlah");
                        long subtotalLong = doc.getLong("subtotal") == null
                                ? 0
                                : doc.getLong("subtotal");

                        int jumlah = jumlahLong == null
                                ? 0
                                : jumlahLong.intValue();

                        db.collection("menu")
                                .document(idMenu)
                                .get()
                                .addOnSuccessListener(menuDoc -> {

                                    String namaMenu =
                                            menuDoc.getString("nama_menu");

                                    if (namaMenu == null) {
                                        namaMenu = "Menu tidak ditemukan";
                                    }

                                    ArrayList<AddonDetailModel> addons =
                                            new ArrayList<>();

                                    PesananBelumDetailModel model =
                                            new PesananBelumDetailModel(
                                                    namaMenu,
                                                    jumlah,
                                                    subtotalLong,
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

                                                android.util.Log.d(
                                                        "DETAIL_ORDER",
                                                        "Addon untuk detail "
                                                                + doc.getId()
                                                                + " = "
                                                                + addonQuery.size()
                                                );

                                                for (DocumentSnapshot addonDoc :
                                                        addonQuery.getDocuments()) {

                                                    String idAddon =
                                                            addonDoc.getString(
                                                                    "id_addon"
                                                            );

                                                    Long qtyLong =
                                                            addonDoc.getLong(
                                                                    "jumlah"
                                                            );

                                                    int qty = qtyLong == null
                                                            ? 0
                                                            : qtyLong.intValue();

                                                    db.collection("addon")
                                                            .document(idAddon)
                                                            .get()
                                                            .addOnSuccessListener(addonData -> {

                                                                String namaAddon =
                                                                        addonData.getString(
                                                                                "nama_addon"
                                                                        );

                                                                if (namaAddon == null) {
                                                                    namaAddon = "-";
                                                                }

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
                })
                .addOnFailureListener(e -> {

                    android.util.Log.e(
                            "DETAIL_ORDER",
                            "Gagal load detail",
                            e
                    );
                });
    }
}