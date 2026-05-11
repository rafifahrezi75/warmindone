package com.example.warmindone.menu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.warmindone.R;
import com.example.warmindone.kasiradmin.DetailMenuActivity;
import com.example.warmindone.kasiradmin.UbahMenuActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    Context context;
    List<MenuModel> list;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MenuAdapter(Context context, List<MenuModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_widget, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuModel model = list.get(position);

        holder.tvNamaMenu.setText(model.getNamaMenu());
        holder.tvKategori.setText(model.getNamaKategori());

        Glide.with(context)
                .load(model.getImageUrl())
                .placeholder(R.drawable.ic_menu)
                .into(holder.imgMenu);

        // KLIK ITEM (Buka Detail)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailMenuActivity.class);
            intent.putExtra("id", model.getId());
            context.startActivity(intent);
        });

        // EDIT
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, UbahMenuActivity.class);
            intent.putExtra("id", model.getId());
            context.startActivity(intent);
        });

        // DELETE
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Hapus Menu")
                    .setMessage("Yakin ingin menghapus menu ini?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        db.collection("menu").document(model.getId()).delete()
                                .addOnSuccessListener(unused -> {
                                    int currentPosition = holder.getAdapterPosition();
                                    if (currentPosition != RecyclerView.NO_POSITION) {
                                        list.remove(currentPosition);
                                        notifyItemRemoved(currentPosition);
                                    }
                                    Toast.makeText(context, "Menu berhasil dihapus", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Gagal menghapus menu", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaMenu, tvKategori;
        ImageView imgMenu, btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMenu = itemView.findViewById(R.id.imgMenu);
            tvNamaMenu = itemView.findViewById(R.id.tvNamaMenu);
            tvKategori = itemView.findViewById(R.id.tvKategori);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}