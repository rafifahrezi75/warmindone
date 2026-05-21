package com.example.warmindone.pesananselesai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PesananSelesaiAdapter extends RecyclerView.Adapter<PesananSelesaiAdapter.ViewHolder> {

    private Context context;
    private List<PesananSelesaiModel> list;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PesananSelesaiAdapter(Context context, List<PesananSelesaiModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.pesananselesai_widget, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PesananSelesaiModel model = list.get(position);

        // INVOICE
        if (model.getId_order() != null) {
            String inv = model.getId_order().length() > 7
                    ? model.getId_order().substring(0, 7)
                    : model.getId_order();

            holder.tvInvoice.setText("INV-" + inv);
        }

        // STATUS
        holder.tvStatus.setText("Lunas");

        // METODE
        holder.tvMetode.setText(model.getMetode());

        // TOTAL
        holder.tvTotal.setText(
                "Rp, " + String.format("%,d",
                        model.getTotal_harga()).replace(',', '.')
        );

        // TANGGAL
        if (model.getTanggal_order() != null) {

            SimpleDateFormat sdf =
                    new SimpleDateFormat("dd MMM yyyy",
                            new Locale("id", "ID"));

            holder.tvTanggal.setText(
                    sdf.format(model.getTanggal_order().toDate())
            );
        }

        // NAMA + EMAIL USER
        if (model.getId_user() != null) {

            db.collection("users")
                    .document(model.getId_user())
                    .get()
                    .addOnSuccessListener(doc -> {

                        if (doc.exists()) {

                            String nama = doc.getString("nama");
                            String email = doc.getString("email");

                            holder.tvNama.setText(
                                    nama != null ? nama : "Tanpa Nama"
                            );

                            holder.tvEmail.setText(
                                    email != null ? email : "-"
                            );

                        } else {

                            holder.tvNama.setText("User tidak ditemukan");
                            holder.tvEmail.setText("-");

                        }
                    })
                    .addOnFailureListener(e -> {

                        holder.tvNama.setText("Error");
                        holder.tvEmail.setText("-");

                    });

        } else {

            holder.tvNama.setText("Guest");
            holder.tvEmail.setText("-");

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvInvoice,
                tvStatus,
                tvMetode,
                tvTotal,
                tvTanggal,
                tvNama,
                tvEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvInvoice = itemView.findViewById(R.id.tvInvoice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvMetode = itemView.findViewById(R.id.tvMetode);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvEmail = itemView.findViewById(R.id.tvEmail);
        }
    }
}