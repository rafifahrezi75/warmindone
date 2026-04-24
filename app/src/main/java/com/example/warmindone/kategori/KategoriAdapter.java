package com.example.warmindone.kategori;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;
import com.example.warmindone.kasiradmin.UbahKategoriActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.ViewHolder> {

    private Context context;
    private List<KategoriModel> list;

    public KategoriAdapter(Context context, List<KategoriModel> list) {
        this.context = context;
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama;
        ImageView btnEdit, btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvnamakategori);
            btnEdit = itemView.findViewById(R.id.btnPenKategori);
            btnDelete = itemView.findViewById(R.id.btnDeleteKategori);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.kategori_widget, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        KategoriModel kategoriModel = list.get(position);

        holder.tvNama.setText(kategoriModel.getKategori());

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, UbahKategoriActivity.class);
            intent.putExtra("id", kategoriModel.getId());
            intent.putExtra("nama", kategoriModel.getKategori());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("kategori")
                    .document(kategoriModel.getId())
                    .delete()
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Berhasil dihapus", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Gagal hapus", Toast.LENGTH_SHORT).show();
                    });
        });

        holder.btnDelete.setOnClickListener(v -> {

            new android.app.AlertDialog.Builder(context)
                    .setTitle("Hapus Kategori")
                    .setMessage("Yakin mau hapus \"" + kategoriModel.getKategori() + "\"?")
                    .setPositiveButton("Hapus", (dialog, which) -> {

                        FirebaseFirestore.getInstance()
                                .collection("kategori")
                                .document(kategoriModel.getId())
                                .delete()
                                .addOnSuccessListener(unused -> {

                                    int pos = holder.getAdapterPosition();
                                    list.remove(pos);
                                    notifyItemRemoved(pos);

                                    Toast.makeText(context, "Berhasil dihapus", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Gagal hapus", Toast.LENGTH_SHORT).show();
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
}