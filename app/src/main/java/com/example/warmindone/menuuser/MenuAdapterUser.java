package com.example.warmindone.menuuser;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.warmindone.R;
import com.example.warmindone.pelanggan.DetailMenuUserActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MenuAdapterUser
        extends RecyclerView.Adapter<MenuAdapterUser.ViewHolder> {

    private final List<MenuModelUser> menuList;
    private final boolean isBestMenu;

    public MenuAdapterUser(
            List<MenuModelUser> menuList,
            boolean isBestMenu) {

        this.menuList = menuList;
        this.isBestMenu = isBestMenu;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        int layout = isBestMenu
                ? R.layout.bestmenu_widget
                : R.layout.menuuser_widget;

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        MenuModelUser menu = menuList.get(position);

        holder.tvNamaMenu.setText(menu.getNama_menu());

        NumberFormat rupiah =
                NumberFormat.getCurrencyInstance(
                        new Locale("id", "ID"));

        holder.tvHarga.setText(
                rupiah.format(menu.getHarga())
        );

        Glide.with(holder.itemView.getContext())
                .load(menu.getFoto())
                .placeholder(R.drawable.ic_menu)
                .into(holder.imgMenu);

        // Klik Card -> Detail
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(
                    holder.itemView.getContext(),
                    DetailMenuUserActivity.class
            );

            // kirim id saja
            intent.putExtra("menuId", menu.getId());

            holder.itemView.getContext()
                    .startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgMenu;
        TextView tvNamaMenu, tvHarga;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgMenu = itemView.findViewById(R.id.imgMenu);
            tvNamaMenu = itemView.findViewById(R.id.tvNamaMenu);
            tvHarga = itemView.findViewById(R.id.tvHarga);
        }
    }
}