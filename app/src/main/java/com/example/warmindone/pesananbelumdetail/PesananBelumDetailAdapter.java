package com.example.warmindone.pesananbelumdetail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;
import com.example.warmindone.addondetail.AddonDetailAdapter;

import java.util.ArrayList;

public class PesananBelumDetailAdapter
        extends RecyclerView.Adapter<PesananBelumDetailAdapter.ViewHolder> {

    private final ArrayList<PesananBelumDetailModel> list;

    public PesananBelumDetailAdapter(
            ArrayList<PesananBelumDetailModel> list
    ) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.pesananbelumdetail_widget,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        PesananBelumDetailModel item =
                list.get(position);

        holder.tvNamaMenu.setText(
                item.getNamaMenu()
        );

        holder.tvQty.setText(
                item.getJumlah() + "x"
        );

        holder.rvItems.setLayoutManager(
                new LinearLayoutManager(
                        holder.itemView.getContext()
                )
        );

        holder.rvItems.setAdapter(
                new AddonDetailAdapter(
                        item.getAddons()
                )
        );
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvNamaMenu;
        TextView tvQty;
        RecyclerView rvItems;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNamaMenu =
                    itemView.findViewById(R.id.tvNamaMenu);

            tvQty =
                    itemView.findViewById(R.id.tvQty);

            rvItems =
                    itemView.findViewById(R.id.rvItems);
        }
    }
}