package com.example.warmindone.pesananselesai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PesananSelesaiAdapter
        extends RecyclerView.Adapter<PesananSelesaiAdapter.ViewHolder> {

    private Context context;
    private List<PesananSelesaiModel> list;

    public PesananSelesaiAdapter(Context context,
                                 List<PesananSelesaiModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.pesananselesai_widget, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        PesananSelesaiModel model = list.get(position);

        // INVOICE
        if (model.getId_order() != null &&
                model.getId_order().length() >= 7) {

            holder.tvInvoice.setText(
                    "INV-" + model.getId_order().substring(0, 7)
            );
        }

        // STATUS
        holder.tvStatus.setText("Lunas");

        // METODE
        holder.tvMetode.setText(model.getMetode());

        // TOTAL
        holder.tvTotal.setText(
                "Rp " +
                        String.format("%,d",
                                        model.getTotal_harga())
                                .replace(',', '.')
        );

        // TANGGAL
        if (model.getTanggal_order() != null) {

            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "dd MMM yyyy",
                            new Locale("id", "ID")
                    );

            holder.tvTanggal.setText(
                    sdf.format(model.getTanggal_order().toDate())
            );
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvInvoice, tvStatus, tvMetode,
                tvTotal, tvTanggal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvInvoice = itemView.findViewById(R.id.tvInvoice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvMetode = itemView.findViewById(R.id.tvMetode);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
        }
    }
}