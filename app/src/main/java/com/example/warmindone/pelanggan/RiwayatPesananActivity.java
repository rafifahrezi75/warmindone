package com.example.warmindone.pelanggan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;
import com.example.warmindone.riwayatpesanan.RiwayatPesananAdapter;
import com.example.warmindone.riwayatpesanan.RiwayatPesananModel;
import com.example.warmindone.riwayatpesanan.RiwayatPesananAddonModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RiwayatPesananActivity extends AppCompatActivity {

    private TextView tvInvoice;
    private TextView tvTanggal;
    private TextView tvNamaPelanggan;
    private TextView tvTotalBayar;

    private RecyclerView rvPesanan;
    private ImageButton btnBack;

    private FirebaseFirestore db;

    private ArrayList<RiwayatPesananModel> listPesanan;
    private RiwayatPesananAdapter adapter;

    private String idOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayatpesan);

        db = FirebaseFirestore.getInstance();

        tvInvoice = findViewById(R.id.tvInvoice);
        tvTanggal = findViewById(R.id.tvTanggal);
        tvNamaPelanggan = findViewById(R.id.tvNamaPelanggan);
        tvTotalBayar = findViewById(R.id.tvTotalBayar);

        rvPesanan = findViewById(R.id.rvPesanan);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        findViewById(R.id.btnBayar)
                .setOnClickListener(v -> cekStatusPembayaran());

        listPesanan = new ArrayList<>();

        adapter = new RiwayatPesananAdapter(
                this,
                listPesanan
        );

        rvPesanan.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvPesanan.setAdapter(adapter);

        idOrder = getIntent().getStringExtra("id_order");

        if (idOrder != null) {
            loadOrder();
        }
    }

    private void cekStatusPembayaran() {

        db.collection("orders")
                .document(idOrder)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    String status =
                            doc.getString("status");

                    if ("lunas".equalsIgnoreCase(status)) {

                        bukaDashboard();

                    } else {

                        android.widget.Toast.makeText(
                                this,
                                "Pesanan belum dibayar",
                                android.widget.Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void bukaDashboard() {

        Intent intent =
                new Intent(
                        RiwayatPesananActivity.this,
                        DashboardUser.class
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK
        );

        startActivity(intent);
        finish();
    }

    private void loadOrder() {

        db.collection("orders")
                .document(idOrder)
                .get()
                .addOnSuccessListener(orderDoc -> {

                    if (!orderDoc.exists()) return;

                    tvInvoice.setText(idOrder);

                    NumberFormat rupiah =
                            NumberFormat.getCurrencyInstance(
                                    new Locale("id", "ID")
                            );

                    Long totalHarga =
                            orderDoc.getLong(
                                    "total_harga"
                            );

                    if (totalHarga == null) {
                        totalHarga = 0L;
                    }

                    tvTotalBayar.setText(
                            rupiah.format(totalHarga)
                    );

                    com.google.firebase.Timestamp ts =
                            orderDoc.getTimestamp(
                                    "tanggal_order"
                            );

                    if (ts != null) {

                        Date date = ts.toDate();

                        SimpleDateFormat sdf =
                                new SimpleDateFormat(
                                        "dd MMM yyyy HH:mm",
                                        new Locale("id", "ID")
                                );

                        tvTanggal.setText(
                                sdf.format(date)
                        );
                    }

                    String idUser =
                            orderDoc.getString(
                                    "id_user"
                            );

                    loadUser(idUser);

                    loadDetailPesanan();
                });
    }

    private void loadUser(String idUser) {

        db.collection("users")
                .document(idUser)
                .get()
                .addOnSuccessListener(userDoc -> {

                    if (userDoc.exists()) {

                        tvNamaPelanggan.setText(
                                userDoc.getString("nama")
                        );
                    }
                });
    }

    private void loadDetailPesanan() {

        db.collection("orders_detail")
                .whereEqualTo(
                        "id_order",
                        idOrder
                )
                .get()
                .addOnSuccessListener(query -> {

                    listPesanan.clear();

                    for (DocumentSnapshot detailDoc :
                            query.getDocuments()) {

                        RiwayatPesananModel model =
                                new RiwayatPesananModel();

                        model.setIdDetail(
                                detailDoc.getId()
                        );

                        model.setIdMenu(
                                detailDoc.getString(
                                        "id_menu"
                                )
                        );

                        model.setJumlah(
                                detailDoc.getLong(
                                        "jumlah"
                                )
                        );

                        db.collection("menu")
                                .document(
                                        model.getIdMenu()
                                )
                                .get()
                                .addOnSuccessListener(menuDoc -> {

                                    if(menuDoc.exists()) {

                                        model.setNamaMenu(
                                                menuDoc.getString(
                                                        "nama_menu"
                                                )
                                        );
                                    }

                                    loadAddon(model);
                                });
                    }
                });
    }
    private void loadAddon(
            RiwayatPesananModel model) {

        db.collection("order_detail_addon")
                .whereEqualTo(
                        "id_detail",
                        model.getIdDetail()
                )
                .get()
                .addOnSuccessListener(query -> {

                    ArrayList<RiwayatPesananAddonModel>
                            addonList =
                            new ArrayList<>();

                    if(query.isEmpty()) {

                        model.setAddons(
                                addonList
                        );

                        listPesanan.add(model);

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

                                    RiwayatPesananAddonModel addon =
                                            new RiwayatPesananAddonModel();

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

                                    if(!listPesanan.contains(model)) {

                                        listPesanan.add(
                                                model
                                        );
                                    }

                                    adapter.notifyDataSetChanged();
                                });
                    }
                });
    }
}