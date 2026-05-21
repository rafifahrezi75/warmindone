package com.example.warmindone.addon;

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
import com.example.warmindone.kasiradmin.UbahAddonActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AddonAdapter extends RecyclerView.Adapter<AddonAdapter.ViewHolder> {

    Context context;
    ArrayList<AddonModel> list;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AddonAdapter(Context context, ArrayList<AddonModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.addon_widget, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddonModel model = list.get(position);

        // Perbaikan: Menggunakan getter yang sesuai dengan database (nama_addon, stok, fotoaddon)
        holder.tvNama.setText(model.getNama_addon());
        holder.tvStok.setText("Stok : " + model.getStok());

        Glide.with(context)
                .load(model.getFotoaddon())
                .placeholder(R.drawable.ic_menu)
                .into(holder.imgAddon);

        // Tombol Edit
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, UbahAddonActivity.class);
            intent.putExtra("id", model.getId());
            context.startActivity(intent);
        });

        // Tombol Hapus
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Hapus Addon")
                    .setMessage("Yakin ingin menghapus addon ini?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        if (model.getId() != null) {
                            db.collection("addon").document(model.getId())
                                    .delete()
                                    .addOnSuccessListener(unused -> {
                                        int currentPosition = holder.getAdapterPosition();
                                        if (currentPosition != RecyclerView.NO_POSITION) {
                                            list.remove(currentPosition);
                                            notifyItemRemoved(currentPosition);
                                        }
                                        Toast.makeText(context, "Addon berhasil dihapus", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Gagal menghapus: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
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
        ImageView imgAddon, btnEdit, btnDelete;
        TextView tvNama, tvStok;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAddon = itemView.findViewById(R.id.imgAddon);
            btnEdit = itemView.findViewById(R.id.btnEditAddon);
            btnDelete = itemView.findViewById(R.id.btnDeleteAddon);
            tvNama = itemView.findViewById(R.id.tvNamaAddon);
            tvStok = itemView.findViewById(R.id.tvStokAddon);
        }
    }
}
