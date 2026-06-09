package com.example.warmindone.pelanggan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;
import com.example.warmindone.addonuser.AddonUserModel;
import com.example.warmindone.cart.CartAdapter;
import com.example.warmindone.cart.CartModel;
import com.example.warmindone.menuuser.MenuModelUser;
import com.example.warmindone.pelanggan.DashboardUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CheckoutActivity
        extends AppCompatActivity {

    private RecyclerView rvCheckout;

    private TextView tvHargaPembayaran;
    private TextView tvDiskonPembayaran;
    private TextView tvTotalPembayaran;

    private ArrayList<CartModel> cartList;
    private CartAdapter cartAdapter;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        rvCheckout =
                findViewById(R.id.rvCheckout);

        tvHargaPembayaran =
                findViewById(R.id.tvHargaPembayaran);

        tvDiskonPembayaran =
                findViewById(R.id.tvDiskonPembayaran);

        tvTotalPembayaran =
                findViewById(R.id.tvTotalPembayaran);

        findViewById(R.id.btnBackCheckout).setOnClickListener(v -> {
            Intent intent = new Intent(
                    CheckoutActivity.this,
                    DashboardUser.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
            finish();
        });

        db = FirebaseFirestore.getInstance();

        cartList = new ArrayList<>();

        cartAdapter =
                new CartAdapter(
                        this,
                        cartList,
                        this::updateSummary
                );

        rvCheckout.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvCheckout.setAdapter(cartAdapter);

        findViewById(R.id.btnTambahLagi)
                .setOnClickListener(v -> finish());

        findViewById(R.id.btnPesanSekarang)
                .setOnClickListener(v -> pesanSekarang());

        loadCart();
    }

    private void loadCart() {

        if (FirebaseAuth.getInstance()
                .getCurrentUser() == null) {
            return;
        }

        String userId =
                FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getUid();

        db.collection("keranjang")
                .whereEqualTo(
                        "id_user",
                        userId
                )
                .get()
                .addOnSuccessListener(query -> {

                    cartList.clear();

                    if (query.isEmpty()) {

                        cartAdapter.notifyDataSetChanged();
                        updateSummary();
                        return;
                    }

                    query.getDocuments()
                            .forEach(doc -> {

                                CartModel cart =
                                        doc.toObject(
                                                CartModel.class);

                                if (cart == null) return;

                                cart.setId(
                                        doc.getId()
                                );

                                db.collection("menu")
                                        .document(
                                                cart.getId_menu()
                                        )
                                        .get()
                                        .addOnSuccessListener(menuDoc -> {

                                            MenuModelUser menu =
                                                    menuDoc.toObject(
                                                            MenuModelUser.class);

                                            if (menu != null) {

                                                cart.setNamaMenu(
                                                        menu.getNama_menu()
                                                );

                                                cart.setFotoMenu(
                                                        menu.getFoto()
                                                );

                                                cart.setStokMenu(
                                                        menu.getStok()
                                                );

                                            }

                                            loadAddonKeranjang(
                                                    doc.getId(),
                                                    cart
                                            );
                                        });
                            });
                });
    }

    private void loadAddonKeranjang(
            String keranjangId,
            CartModel cart) {

        db.collection("keranjang_detail")
                .whereEqualTo(
                        "id_keranjang",
                        keranjangId
                )
                .get()
                .addOnSuccessListener(detailQuery -> {

                    ArrayList<AddonUserModel> addons =
                            new ArrayList<>();

                    if (detailQuery.isEmpty()) {

                        cart.setAddons(addons);

                        cartList.add(cart);

                        cartAdapter.notifyDataSetChanged();

                        updateSummary();

                        return;
                    }

                    final int totalDetail =
                            detailQuery.size();

                    final int[] loaded = {0};

                    detailQuery.getDocuments()
                            .forEach(detailDoc -> {

                                String idAddon =
                                        detailDoc.getString(
                                                "id_addon"
                                        );

                                if (idAddon == null) {

                                    loaded[0]++;

                                    if (loaded[0]
                                            >= totalDetail) {

                                        cart.setAddons(addons);

                                        cartList.add(cart);

                                        cartAdapter.notifyDataSetChanged();

                                        updateSummary();
                                    }

                                    return;
                                }

                                db.collection("addon")
                                        .document(idAddon)
                                        .get()
                                        .addOnSuccessListener(addonDoc -> {

                                            AddonUserModel addon =
                                                    addonDoc.toObject(
                                                            AddonUserModel.class);

                                            if (addon != null) {

                                                addon.setId(
                                                        addonDoc.getId()
                                                );

                                                addons.add(addon);
                                            }

                                            loaded[0]++;

                                            if (loaded[0]
                                                    >= totalDetail) {

                                                cart.setAddons(addons);

                                                cartList.add(cart);

                                                cartAdapter.notifyDataSetChanged();

                                                updateSummary();
                                            }
                                        });
                            });
                });
    }

    private void updateSummary() {

        long total = 0;

        for (CartModel cart : cartList) {

            if (cart.getHarga() != null) {

                total += cart.getHarga();
            }
        }

        NumberFormat rupiah =
                NumberFormat.getCurrencyInstance(
                        new Locale("id", "ID")
                );

        tvHargaPembayaran.setText(
                rupiah.format(total)
        );

        tvDiskonPembayaran.setText(
                rupiah.format(0)
        );

        tvTotalPembayaran.setText(
                rupiah.format(total)
        );
    }

    private void pesanSekarang() {

        String userId =
                FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getUid();

        long totalHarga = 0;

        for (CartModel cart : cartList) {

            totalHarga += cart.getHarga();
        }

        java.util.HashMap<String, Object> order =
                new java.util.HashMap<>();

        order.put("id_user", userId);
        order.put("total_harga", totalHarga);
        order.put("bayar", totalHarga);
        order.put("kembali", 0);
        order.put("metode", "tunai");
        order.put("status", "pending");
        order.put(
                "tanggal_order",
                com.google.firebase.Timestamp.now()
        );

        db.collection("orders")
                .add(order)
                .addOnSuccessListener(orderRef -> {

                    String orderId =
                            orderRef.getId();

                    for (CartModel cart : cartList) {

                        java.util.HashMap<String,Object>
                                detail =
                                new java.util.HashMap<>();

                        detail.put(
                                "id_order",
                                orderId
                        );

                        detail.put(
                                "id_menu",
                                cart.getId_menu()
                        );

                        detail.put(
                                "jumlah",
                                cart.getJumlah()
                        );

                        detail.put(
                                "subtotal",
                                cart.getHarga()
                        );

                        db.collection("orders_detail")
                                .add(detail)
                                .addOnSuccessListener(
                                        detailRef -> {

                                            String detailId =
                                                    detailRef.getId();

                                            for (AddonUserModel addon
                                                    : cart.getAddons()) {

                                                java.util.HashMap<String,Object>
                                                        addonMap =
                                                        new java.util.HashMap<>();

                                                addonMap.put(
                                                        "id_detail",
                                                        detailId
                                                );

                                                addonMap.put(
                                                        "id_addon",
                                                        addon.getId()
                                                );

                                                addonMap.put(
                                                        "jumlah",
                                                        1
                                                );

                                                addonMap.put(
                                                        "subtotal",
                                                        addon.getHarga()
                                                );

                                                db.collection(
                                                                "order_detail_addon")
                                                        .add(addonMap);
                                            }
                                        });
                    }

                    hapusKeranjang(orderId);
                });
    }

    private void hapusKeranjang(
            String orderId) {

        db.collection("keranjang")
                .whereEqualTo(
                        "id_user",
                        FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getUid()
                )
                .get()
                .addOnSuccessListener(query -> {

                    for (com.google.firebase.firestore.DocumentSnapshot doc
                            : query.getDocuments()) {

                        String keranjangId =
                                doc.getId();

                        db.collection("keranjang_detail")
                                .whereEqualTo(
                                        "id_keranjang",
                                        keranjangId
                                )
                                .get()
                                .addOnSuccessListener(detail -> {

                                    for (com.google.firebase.firestore.DocumentSnapshot d
                                            : detail.getDocuments()) {

                                        d.getReference().delete();
                                    }
                                });

                        doc.getReference().delete();
                    }

                    Intent intent =
                            new Intent(
                                    CheckoutActivity.this,
                                    RiwayatPesananActivity.class
                            );

                    intent.putExtra(
                            "id_order",
                            orderId
                    );

                    startActivity(intent);

                    finish();
                });
    }
}