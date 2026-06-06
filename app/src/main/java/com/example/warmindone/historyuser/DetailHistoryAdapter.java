package com.example.warmindone.historyuser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;

import java.util.ArrayList;

public class DetailHistoryAdapter
        extends RecyclerView.Adapter<
        DetailHistoryAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<DetailHistoryModel>
            list;

    public DetailHistoryAdapter(
            Context context,
            ArrayList<DetailHistoryModel> list) {

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
                                R.layout.itempesanan_widget,
                                parent,
                                false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        DetailHistoryModel item =
                list.get(position);

        holder.tvNamaMenu.setText(
                item.getNamaMenu());

        holder.tvQty.setText(
                item.getJumlah() + "x");

        holder.rvPesananAddon.setLayoutManager(
                new LinearLayoutManager(
                        context));

        holder.rvPesananAddon.setAdapter(
                new DetailHistoryAddonAdapter(
                        item.getAddons()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvNamaMenu;
        TextView tvQty;
        RecyclerView rvPesananAddon;

        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvNamaMenu =
                    itemView.findViewById(
                            R.id.tvNamaMenu);

            tvQty =
                    itemView.findViewById(
                            R.id.tvQty);

            rvPesananAddon =
                    itemView.findViewById(
                            R.id.rvPesananAddon);
        }
    }
}