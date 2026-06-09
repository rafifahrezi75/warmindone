package com.example.warmindone.pendapatan;

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

public class PendapatanAdapter extends RecyclerView.Adapter<PendapatanAdapter.ViewHolder> {

    private Context context;
    private List<PendapatanModel> list;

    public PendapatanAdapter(Context context, List<PendapatanModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.pendapatan_widget, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PendapatanModel model = list.get(position);

        // Format tanggal
        if (model.getTanggal() != null) {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));

            String formattedDate =
                    sdf.format(model.getTanggal().toDate());

            holder.tvTanggal.setText(formattedDate);
        }

        // Format Rupiah
        String totalFormatted =
                String.format("%,d", model.getTotal())
                        .replace(',', '.');

        holder.tvTotalPendapatan.setText(
                "Rp " + totalFormatted
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTanggal;
        TextView tvTotalPendapatan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvTotalPendapatan = itemView.findViewById(R.id.tvTotalPendapatan);
        }
    }
}