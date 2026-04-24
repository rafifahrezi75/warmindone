package com.example.warmindone.kategori;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;

import java.util.List;

import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

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

        // EDIT
        holder.btnEdit.setOnClickListener(v -> {
            Toast.makeText(context, "Edit: " + kategoriModel.getKategori(), Toast.LENGTH_SHORT).show();
        });

        // DELETE
        holder.btnDelete.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("kategori")
                    .document(kategoriModel.getId())
                    .delete();

            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}