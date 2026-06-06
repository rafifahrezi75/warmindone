package com.example.warmindone.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.warmindone.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CartAdapter
        extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    public interface OnCartChanged {
        void onCartUpdated();
    }

    private final Context context;
    private final ArrayList<CartModel> list;
    private final OnCartChanged listener;

    public CartAdapter(
            Context context,
            ArrayList<CartModel> list,
            OnCartChanged listener) {

        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.cart_widget,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        CartModel cart = list.get(position);

        holder.tvOrderName.setText(
                cart.getNamaMenu()
        );

        holder.tvQty.setText(
                String.valueOf(
                        cart.getJumlah()
                )
        );

        NumberFormat rupiah =
                NumberFormat.getCurrencyInstance(
                        new Locale("id", "ID")
                );

        holder.tvOrderPrice.setText(
                rupiah.format(
                        cart.getHarga()
                )
        );

        Glide.with(context)
                .load(cart.getFotoMenu())
                .placeholder(R.drawable.ic_menu)
                .into(holder.imgOrderImage);

        holder.rvAddonCart.setLayoutManager(
                new LinearLayoutManager(
                        context,
                        RecyclerView.VERTICAL,
                        false
                )
        );

        holder.rvAddonCart.setNestedScrollingEnabled(false);

        if (cart.getAddons() == null
                || cart.getAddons().isEmpty()) {

            holder.rvAddonCart.setVisibility(
                    View.GONE
            );

        } else {

            holder.rvAddonCart.setVisibility(
                    View.VISIBLE
            );

            holder.rvAddonCart.setAdapter(
                    new CartAddonAdapter(
                            cart.getAddons()
                    )
            );
        }

        // MINUS
        holder.btnMinusQty.setOnClickListener(v -> {

            FirebaseFirestore db =
                    FirebaseFirestore.getInstance();

            long qty = cart.getJumlah();

            db.collection("menu")
                    .document(cart.getId_menu())
                    .get()
                    .addOnSuccessListener(menuDoc -> {

                        final long stok =
                                menuDoc.getLong("stok") == null
                                        ? 0L
                                        : menuDoc.getLong("stok");

                        if (qty <= 1) {

                            long finalStok = stok;

                            db.collection("keranjang_detail")
                                    .whereEqualTo(
                                            "id_keranjang",
                                            cart.getId()
                                    )
                                    .get()
                                    .addOnSuccessListener(detailQuery -> {

                                        for(DocumentSnapshot d :
                                                detailQuery.getDocuments()) {

                                            d.getReference().delete();
                                        }

                                        db.collection("keranjang")
                                                .document(cart.getId())
                                                .delete()
                                                .addOnSuccessListener(unused -> {

                                                    db.collection("menu")
                                                            .document(cart.getId_menu())
                                                            .update(
                                                                    "stok",
                                                                    finalStok + 1
                                                            );

                                                    int pos =
                                                            holder.getAdapterPosition();

                                                    if(pos != RecyclerView.NO_POSITION){

                                                        list.remove(pos);

                                                        notifyItemRemoved(pos);

                                                        if(listener != null){
                                                            listener.onCartUpdated();
                                                        }
                                                    }
                                                });
                                    });

                            return;
                        }

                        long hargaSatuan =
                                cart.getHarga()
                                        / cart.getJumlah();

                        long qtyBaru =
                                qty - 1;

                        long totalBaru =
                                hargaSatuan * qtyBaru;

                        db.collection("keranjang")
                                .document(cart.getId())
                                .update(
                                        "jumlah", qtyBaru,
                                        "harga", totalBaru
                                );

                        db.collection("menu")
                                .document(cart.getId_menu())
                                .update(
                                        "stok",
                                        stok + 1
                                );

                        db.collection("keranjang_detail")
                                .whereEqualTo(
                                        "id_keranjang",
                                        cart.getId()
                                )
                                .get()
                                .addOnSuccessListener(q -> {

                                    for(DocumentSnapshot d :
                                            q.getDocuments()) {

                                        Long jumlahAddon =
                                                d.getLong("jumlah");

                                        if(jumlahAddon == null)
                                            jumlahAddon = 1L;

                                        Long hargaAddon =
                                                d.getLong("harga");

                                        if(hargaAddon == null)
                                            hargaAddon = 0L;

                                        long hargaSatuanAddon =
                                                hargaAddon /
                                                        Math.max(1,jumlahAddon);

                                        d.getReference().update(
                                                "jumlah",
                                                jumlahAddon - 1,
                                                "harga",
                                                hargaSatuanAddon *
                                                        (jumlahAddon - 1)
                                        );
                                    }
                                });

                        cart.setJumlah(qtyBaru);
                        cart.setHarga(totalBaru);

                        holder.tvQty.setText(
                                String.valueOf(qtyBaru)
                        );

                        holder.tvOrderPrice.setText(
                                NumberFormat
                                        .getCurrencyInstance(
                                                new Locale("id","ID")
                                        )
                                        .format(totalBaru)
                        );

                        if(listener != null){
                            listener.onCartUpdated();
                        }
                    });
        });

        // PLUS
        holder.btnPlusQty.setOnClickListener(v -> {

            FirebaseFirestore db =
                    FirebaseFirestore.getInstance();

            db.collection("menu")
                    .document(cart.getId_menu())
                    .get()
                    .addOnSuccessListener(menuDoc -> {

                        final long stok =
                                menuDoc.getLong("stok") == null
                                        ? 0L
                                        : menuDoc.getLong("stok");

                        if(stok <= 0){

                            android.widget.Toast.makeText(
                                    context,
                                    "Stok tidak cukup",
                                    android.widget.Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        long hargaSatuan =
                                cart.getHarga()
                                        / cart.getJumlah();

                        long qtyBaru =
                                cart.getJumlah() + 1;

                        long totalBaru =
                                hargaSatuan * qtyBaru;

                        db.collection("keranjang")
                                .document(cart.getId())
                                .update(
                                        "jumlah", qtyBaru,
                                        "harga", totalBaru
                                )
                                .addOnSuccessListener(unused -> {

                                    db.collection("menu")
                                            .document(cart.getId_menu())
                                            .update(
                                                    "stok",
                                                    stok - 1
                                            );

                                    db.collection("keranjang_detail")
                                            .whereEqualTo(
                                                    "id_keranjang",
                                                    cart.getId()
                                            )
                                            .get()
                                            .addOnSuccessListener(q -> {

                                                for(DocumentSnapshot d :
                                                        q.getDocuments()) {

                                                    Long jumlahAddon =
                                                            d.getLong("jumlah");

                                                    if(jumlahAddon == null)
                                                        jumlahAddon = 0L;

                                                    Long hargaAddon =
                                                            d.getLong("harga");

                                                    if(hargaAddon == null)
                                                        hargaAddon = 0L;

                                                    long hargaSatuanAddon =
                                                            jumlahAddon == 0
                                                                    ? hargaAddon
                                                                    : hargaAddon / jumlahAddon;

                                                    d.getReference().update(
                                                            "jumlah",
                                                            jumlahAddon + 1,
                                                            "harga",
                                                            hargaSatuanAddon *
                                                                    (jumlahAddon + 1)
                                                    );
                                                }
                                            });

                                    cart.setJumlah(qtyBaru);
                                    cart.setHarga(totalBaru);

                                    holder.tvQty.setText(
                                            String.valueOf(qtyBaru)
                                    );

                                    holder.tvOrderPrice.setText(
                                            NumberFormat
                                                    .getCurrencyInstance(
                                                            new Locale("id","ID")
                                                    )
                                                    .format(totalBaru)
                                    );

                                    if(listener != null){
                                        listener.onCartUpdated();
                                    }
                                });
                    });
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvOrderName;
        TextView tvOrderPrice;
        TextView tvQty;

        ImageView imgOrderImage;

        RecyclerView rvAddonCart;

        ImageButton btnPlusQty;
        ImageButton btnMinusQty;

        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvOrderName =
                    itemView.findViewById(
                            R.id.tvOrderName);

            tvOrderPrice =
                    itemView.findViewById(
                            R.id.tvOrderPrice);

            tvQty =
                    itemView.findViewById(
                            R.id.tvQty);

            imgOrderImage =
                    itemView.findViewById(
                            R.id.imgOrderImage);

            rvAddonCart =
                    itemView.findViewById(
                            R.id.rvAddonCart);

            btnPlusQty =
                    itemView.findViewById(
                            R.id.btnPlusQty);

            btnMinusQty =
                    itemView.findViewById(
                            R.id.btnMinusQty);
        }
    }
}