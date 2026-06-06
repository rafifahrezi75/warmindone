package com.example.warmindone.history;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;
import com.example.warmindone.pelanggan.DetailHistoryActivity;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class HistoryAdapter
        extends RecyclerView.Adapter<
        HistoryAdapter.ViewHolder> {

    private final Context context;
    private final java.util.ArrayList<
            HistoryModel> list;

    public HistoryAdapter(
            Context context,
            java.util.ArrayList<HistoryModel> list) {

        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.itemhistory_widget,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        HistoryModel item =
                list.get(position);

        holder.tvStatus.setText(
                item.getStatus()
        );

        holder.tvNamaMenu.setText(
                item.getNamaMenu()
        );

        holder.tvJumlah.setText(
                item.getJumlah() + "x"
        );

        NumberFormat rupiah =
                NumberFormat.getCurrencyInstance(
                        new Locale("id", "ID")
                );

        holder.tvTotalHarga.setText(
                rupiah.format(
                        item.getTotalHarga()
                )
        );

        holder.tvItemCount.setText(
                "1 item"
        );

        if(item.getTanggalOrder() != null) {

            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "dd MMM yyyy HH:mm",
                            new Locale("id", "ID")
                    );

            holder.tvTanggal.setText(
                    sdf.format(
                            item.getTanggalOrder()
                                    .toDate()
                    )
            );
        }

        holder.rvAddon.setLayoutManager(
                new LinearLayoutManager(
                        context
                )
        );

        holder.rvAddon.setNestedScrollingEnabled(
                false
        );

        holder.rvAddon.setAdapter(
                new HistoryAddonAdapter(
                        item.getAddons()
                )
        );

        holder.btnSelengkapnya
                .setOnClickListener(v -> {

                    Intent intent =
                            new Intent(
                                    context,
                                    DetailHistoryActivity.class
                            );

                    intent.putExtra(
                            "id_order",
                            item.getIdOrder()
                    );

                    context.startActivity(
                            intent
                    );
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvStatus;
        TextView tvTanggal;
        TextView tvNamaMenu;
        TextView tvJumlah;
        TextView tvItemCount;
        TextView tvTotalHarga;

        RecyclerView rvAddon;

        AppCompatButton btnSelengkapnya;

        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvStatus =
                    itemView.findViewById(
                            R.id.tvStatus
                    );

            tvTanggal =
                    itemView.findViewById(
                            R.id.tvTanggal
                    );

            tvNamaMenu =
                    itemView.findViewById(
                            R.id.tvNamaMenu
                    );

            tvJumlah =
                    itemView.findViewById(
                            R.id.tvJumlah
                    );

            tvItemCount =
                    itemView.findViewById(
                            R.id.tvItemCount
                    );

            tvTotalHarga =
                    itemView.findViewById(
                            R.id.tvTotalHarga
                    );

            rvAddon =
                    itemView.findViewById(
                            R.id.rvAddon
                    );

            btnSelengkapnya =
                    itemView.findViewById(
                            R.id.btnSelengkapnya
                    );
        }
    }
}