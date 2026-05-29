package com.example.warmindone.kasiradmin;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;
import com.example.warmindone.addondetail.AddonDetailModel;
import com.example.warmindone.pesanansudahdetail.PesananSudahDetailAdapter;
import com.example.warmindone.pesanansudahdetail.PesananSudahDetailModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PesananSudahDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerDetailPesanan;

    private TextView tvStatus;
    private TextView tvPelanggan;
    private TextView tvTotalBayar;
    private TextView tvBayar;
    private TextView tvKembali;
    private TextView tvInvoice;

    private FirebaseFirestore db;

    private ArrayList<PesananSudahDetailModel> list;
    private PesananSudahDetailAdapter adapter;

    private String idOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanansudahdetail);

        db = FirebaseFirestore.getInstance();

        ImageView btnBack =
                findViewById(R.id.btnBack);

        recyclerDetailPesanan =
                findViewById(R.id.recyclerDetailPesanan);

        tvStatus =
                findViewById(R.id.tvStatus);

        tvPelanggan =
                findViewById(R.id.tvPelanggan);

        tvTotalBayar =
                findViewById(R.id.tvTotalBayar);

        tvBayar =
                findViewById(R.id.tvBayar);

        tvKembali =
                findViewById(R.id.tvKembali);

        tvInvoice =
                findViewById(R.id.tvInvoice);

        btnBack.setOnClickListener(v -> finish());

        list = new ArrayList<>();

        adapter =
                new PesananSudahDetailAdapter(list);

        recyclerDetailPesanan.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerDetailPesanan.setAdapter(adapter);

        idOrder =
                getIntent().getStringExtra("id_order");

        loadHeader();
        loadDetailPesanan();
    }

    private void loadHeader() {

        db.collection("orders")
                .document(idOrder)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    tvStatus.setText("Lunas");

                    tvInvoice.setText(
                            "INV-"
                                    + idOrder.substring(
                                    0,
                                    Math.min(7,
                                            idOrder.length())
                            )
                    );

                    long total =
                            getLong(doc, "total_harga");

                    long bayar =
                            getLong(doc, "bayar");

                    long kembali =
                            getLong(doc, "kembali");

                    tvTotalBayar.setText(
                            rupiah(total)
                    );

                    tvBayar.setText(
                            rupiah(bayar)
                    );

                    tvKembali.setText(
                            rupiah(kembali)
                    );

                    String idUser =
                            doc.getString("id_user");

                    if (idUser != null) {

                        db.collection("users")
                                .document(idUser)
                                .get()
                                .addOnSuccessListener(userDoc -> {

                                    String nama =
                                            userDoc.getString(
                                                    "nama"
                                            );

                                    if (nama != null) {
                                        tvPelanggan.setText(
                                                nama
                                        );
                                    }
                                });
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

                    list.clear();

                    for (DocumentSnapshot doc :
                            query.getDocuments()) {

                        String idMenu =
                                doc.getString(
                                        "id_menu"
                                );

                        int jumlah =
                                doc.getLong(
                                        "jumlah"
                                ).intValue();

                        long subtotal =
                                doc.getLong(
                                        "subtotal"
                                );

                        db.collection("menu")
                                .document(idMenu)
                                .get()
                                .addOnSuccessListener(menuDoc -> {

                                    String namaMenu =
                                            menuDoc.getString(
                                                    "nama_menu"
                                            );

                                    ArrayList<AddonDetailModel>
                                            addons =
                                            new ArrayList<>();

                                    PesananSudahDetailModel model =
                                            new PesananSudahDetailModel(
                                                    namaMenu,
                                                    jumlah,
                                                    subtotal,
                                                    addons
                                            );

                                    list.add(model);

                                    adapter.notifyDataSetChanged();

                                    db.collection(
                                                    "order_detail_addon")
                                            .whereEqualTo(
                                                    "id_detail",
                                                    doc.getId()
                                            )
                                            .get()
                                            .addOnSuccessListener(
                                                    addonQuery -> {

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
                                                                    .addOnSuccessListener(
                                                                            addonData -> {

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

    private long getLong(
            DocumentSnapshot doc,
            String field
    ) {

        Object value = doc.get(field);

        if (value instanceof Long) {
            return (Long) value;
        }

        if (value instanceof String) {
            return Long.parseLong(
                    (String) value
            );
        }

        return 0;
    }

    private String rupiah(long value) {

        NumberFormat format =
                NumberFormat.getCurrencyInstance(
                        new Locale("id", "ID")
                );

        return format.format(value)
                .replace(",00", "");
    }
}