package com.example.warmindone.pesanansudah;

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

import android.content.Intent;
import com.example.warmindone.kasiradmin.PesananSudahDetailActivity;

public class PesananSudahAdapter extends RecyclerView.Adapter<PesananSudahAdapter.ViewHolder> {

    private Context context;
    private List<PesananSudahModel> list;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PesananSudahAdapter(Context context, List<PesananSudahModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pesanansudah_widget, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PesananSudahModel model = list.get(position);

        if (model.getId_order() != null) {
            String inv = model.getId_order().length() > 7
                    ? model.getId_order().substring(0, 7)
                    : model.getId_order();

            holder.tvInvoice.setText("INV-" + inv);
        }

        holder.tvStatus.setText("Selesai");
        holder.tvMetode.setText(model.getMetode());

        holder.tvTotal.setText(
                "Rp " +
                        String.format("%,d",
                                        model.getTotal_harga())
                                .replace(',', '.')
        );

        if (model.getTanggal_order() != null) {
            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "dd MMM yyyy",
                            new Locale("id", "ID")
                    );

            holder.tvTanggal.setText(
                    sdf.format(
                            model.getTanggal_order()
                                    .toDate()
                    )
            );
        }

        if (model.getId_user() != null) {
            db.collection("users")
                    .document(model.getId_user())
                    .get()
                    .addOnSuccessListener(doc -> {

                        if (doc.exists()) {

                            holder.tvNama.setText(
                                    doc.getString("nama")
                            );

                            holder.tvEmail.setText(
                                    doc.getString("email")
                            );

                        } else {

                            holder.tvNama.setText(
                                    "User tidak ditemukan"
                            );

                            holder.tvEmail.setText("-");
                        }
                    });
        }

        // CLICK KE DETAIL
        holder.itemView.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            context,
                            PesananSudahDetailActivity.class
                    );

            intent.putExtra(
                    "id_order",
                    model.getId_order()
            );

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInvoice, tvStatus, tvMetode, tvTotal, tvTanggal, tvNama, tvEmail;

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
