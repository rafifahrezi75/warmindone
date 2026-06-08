package com.example.warmindone.pelanggan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.warmindone.R;
import com.example.warmindone.addonuser.AddonUserAdapter;
import com.example.warmindone.addonuser.AddonUserModel;
import com.example.warmindone.menuuser.MenuModelUser;
import com.example.warmindone.pelanggan.DashboardUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class DetailMenuUserActivity extends AppCompatActivity {

    private ImageView imgMenu;

    private TextView tvNamaMenu;
    private TextView tvHarga;
    private TextView tvDeskripsi;
    private TextView tvQuantityDetail;

    private RecyclerView rvAddon;

    private FirebaseFirestore db;

    private ArrayList<AddonUserModel> addonList;
    private AddonUserAdapter addonAdapter;

    private String menuId;

    private long hargaMenu = 0;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailmenuitem);

        getOnBackPressedDispatcher().addCallback(this,
                new androidx.activity.OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {

                        Intent intent = new Intent(
                                DetailMenuUserActivity.this,
                                DashboardUser.class
                        );

                        startActivity(intent);
                        finish();
                    }
                });

        findViewById(R.id.btnBackDetail)
                .setOnClickListener(v -> {

                    Intent intent = new Intent(
                            DetailMenuUserActivity.this,
                            DashboardUser.class
                    );

                    startActivity(intent);
                    finish();
                });
        imgMenu = findViewById(R.id.imgDetailMenu);

        tvNamaMenu =
                findViewById(R.id.tvDetailMenuName);

        tvHarga =
                findViewById(R.id.tvDetailMenuPrice);

        tvDeskripsi =
                findViewById(R.id.tvDetailDescription);

        tvQuantityDetail =
                findViewById(R.id.tvQuantityDetail);

        rvAddon =
                findViewById(R.id.rvAddonDetail);

        db = FirebaseFirestore.getInstance();

        addonList = new ArrayList<>();

        addonAdapter =
                new AddonUserAdapter(addonList);

        rvAddon.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvAddon.setAdapter(addonAdapter);

        menuId =
                getIntent().getStringExtra("menuId");

        if (menuId != null) {
            loadMenu(menuId);
            loadAddon();
        }

        findViewById(R.id.btnPlusDetail)
                .setOnClickListener(v -> {

                    quantity++;

                    tvQuantityDetail.setText(
                            String.valueOf(quantity)
                    );
                });

        findViewById(R.id.btnMinusDetail)
                .setOnClickListener(v -> {

                    if (quantity > 1) {

                        quantity--;

                        tvQuantityDetail.setText(
                                String.valueOf(quantity)
                        );
                    }
                });
    }

    public void eekpage(View view) {

        if (FirebaseAuth.getInstance()
                .getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "User belum login",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String userId =
                FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getUid();

        ArrayList<AddonUserModel> selectedAddons =
                addonAdapter.getSelectedAddons();

        long totalAddon = 0;

        for (AddonUserModel addon : selectedAddons) {

            totalAddon += addon.getHarga();
        }

        long totalHarga =
                (hargaMenu + totalAddon)
                        * quantity;

        db.collection("menu")
                .document(menuId)
                .get()
                .addOnSuccessListener(menuDoc -> {

                    Long stokMenu =
                            menuDoc.getLong("stok");

                    if (stokMenu == null)
                        stokMenu = 0L;

                    if (stokMenu < quantity) {

                        Toast.makeText(
                                this,
                                "Stok menu tidak cukup",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    menuDoc.getReference()
                            .update(
                                    "stok",
                                    stokMenu - quantity
                            )
                            .addOnSuccessListener(unused -> {

                                simpanKeranjang(
                                        userId,
                                        selectedAddons,
                                        totalHarga
                                );

                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        this,
                                        "Gagal update stok : "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();

                            });
                });
    }

    private void simpanKeranjang(
            String userId,
            ArrayList<AddonUserModel> selectedAddons,
            long totalHarga) {

        db.collection("keranjang")
                .whereEqualTo("id_user", userId)
                .whereEqualTo("id_menu", menuId)
                .get()
                .addOnSuccessListener(query -> {

                    boolean ditemukan = false;

                    for (com.google.firebase.firestore.DocumentSnapshot cartDoc :
                            query.getDocuments()) {

                        String keranjangId =
                                cartDoc.getId();

                        db.collection("keranjang_detail")
                                .whereEqualTo(
                                        "id_keranjang",
                                        keranjangId
                                )
                                .get()
                                .addOnSuccessListener(detailQuery -> {

                                    ArrayList<String> addonLama =
                                            new ArrayList<>();

                                    for (var d :
                                            detailQuery.getDocuments()) {

                                        String idAddon =
                                                d.getString(
                                                        "id_addon"
                                                );

                                        if (idAddon != null) {
                                            addonLama.add(idAddon);
                                        }
                                    }

                                    ArrayList<String> addonBaru =
                                            new ArrayList<>();

                                    for (AddonUserModel addon :
                                            selectedAddons) {

                                        addonBaru.add(
                                                addon.getId()
                                        );
                                    }

                                    java.util.Collections.sort(
                                            addonLama
                                    );

                                    java.util.Collections.sort(
                                            addonBaru
                                    );

                                    if (addonLama.equals(
                                            addonBaru
                                    )) {

                                        long jumlahLama =
                                                cartDoc.getLong(
                                                        "jumlah"
                                                ) == null
                                                        ? 0
                                                        : cartDoc.getLong(
                                                        "jumlah"
                                                );

                                        long hargaLama =
                                                cartDoc.getLong(
                                                        "harga"
                                                ) == null
                                                        ? 0
                                                        : cartDoc.getLong(
                                                        "harga"
                                                );

                                        cartDoc.getReference()
                                                .update(
                                                        "jumlah",
                                                        jumlahLama + quantity,
                                                        "harga",
                                                        hargaLama + totalHarga
                                                );

                                        Toast.makeText(
                                                DetailMenuUserActivity.this,
                                                "Jumlah diperbarui",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        startActivity(
                                                new Intent(
                                                        DetailMenuUserActivity.this,
                                                        CheckoutActivity.class
                                                )
                                        );

                                        finish();
                                    } else {

                                        buatKeranjangBaru(
                                                userId,
                                                selectedAddons,
                                                totalHarga
                                        );
                                    }
                                });

                        ditemukan = true;
                        break;
                    }

                    if (!ditemukan) {

                        buatKeranjangBaru(
                                userId,
                                selectedAddons,
                                totalHarga
                        );
                    }
                });
    }

    private void buatKeranjangBaru(
            String userId,
            ArrayList<AddonUserModel> selectedAddons,
            long totalHarga) {

        HashMap<String,Object> keranjang =
                new HashMap<>();

        keranjang.put("id_user", userId);
        keranjang.put("id_menu", menuId);
        keranjang.put("jumlah", quantity);
        keranjang.put("harga", totalHarga);

        db.collection("keranjang")
                .add(keranjang)
                .addOnSuccessListener(keranjangRef -> {

                    String keranjangId =
                            keranjangRef.getId();

                    for (AddonUserModel addon :
                            selectedAddons) {

                        HashMap<String,Object> detail =
                                new HashMap<>();

                        detail.put(
                                "id_keranjang",
                                keranjangId
                        );

                        detail.put(
                                "id_addon",
                                addon.getId()
                        );

                        detail.put(
                                "jumlah",
                                quantity
                        );

                        detail.put(
                                "harga",
                                addon.getHarga()
                                        * quantity
                        );

                        db.collection(
                                        "keranjang_detail")
                                .add(detail);
                    }

                    Toast.makeText(
                            DetailMenuUserActivity.this,
                            "Berhasil ditambahkan",
                            Toast.LENGTH_SHORT
                    ).show();

                    startActivity(
                            new Intent(
                                    DetailMenuUserActivity.this,
                                    CheckoutActivity.class
                            )
                    );

                    finish();
                });
    }

    private void loadMenu(String menuId) {

        db.collection("menu")
                .document(menuId)
                .get()
                .addOnSuccessListener(document -> {

                    MenuModelUser menu =
                            document.toObject(
                                    MenuModelUser.class
                            );

                    if (menu == null) return;

                    tvNamaMenu.setText(
                            menu.getNama_menu()
                    );

                    hargaMenu =
                            menu.getHarga();

                    NumberFormat rupiah =
                            NumberFormat.getCurrencyInstance(
                                    new Locale("id", "ID")
                            );

                    tvHarga.setText(
                            rupiah.format(
                                    hargaMenu
                            )
                    );

                    if (menu.getDeskripsi() != null) {

                        tvDeskripsi.setText(
                                menu.getDeskripsi()
                        );
                    }

                    Glide.with(this)
                            .load(menu.getFoto())
                            .placeholder(R.drawable.ic_menu)
                            .into(imgMenu);
                });
    }

    private void loadAddon() {

        db.collection("menu_addon")
                .whereEqualTo(
                        "id_menu",
                        menuId
                )
                .get()
                .addOnSuccessListener(query -> {

                    addonList.clear();

                    if (query.isEmpty()) {

                        addonAdapter
                                .notifyDataSetChanged();

                        return;
                    }

                    query.getDocuments()
                            .forEach(relasi -> {

                                String idAddon =
                                        relasi.getString(
                                                "id_addon"
                                        );

                                if (idAddon == null)
                                    return;

                                db.collection("addon")
                                        .document(idAddon)
                                        .get()
                                        .addOnSuccessListener(addonDoc -> {

                                            if (!addonDoc.exists())
                                                return;

                                            AddonUserModel addon =
                                                    addonDoc.toObject(
                                                            AddonUserModel.class
                                                    );

                                            if (addon == null)
                                                return;

                                            addon.setId(
                                                    addonDoc.getId()
                                            );

                                            addonList.add(addon);

                                            addonAdapter
                                                    .notifyDataSetChanged();
                                        });
                            });
                });
    }
}