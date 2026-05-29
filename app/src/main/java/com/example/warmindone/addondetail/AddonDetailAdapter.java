package com.example.warmindone.addondetail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;

import java.util.ArrayList;

public class AddonDetailAdapter
        extends RecyclerView.Adapter<AddonDetailAdapter.ViewHolder> {

    private final ArrayList<AddonDetailModel> list;

    public AddonDetailAdapter(
            ArrayList<AddonDetailModel> list
    ) {
        this.list = list;
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvItemName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItemName =
                    itemView.findViewById(R.id.tvItemName);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.addondetail_widget,
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

        AddonDetailModel item =
                list.get(position);

        holder.tvItemName.setText(
                item.getJumlah()
                        + "x "
                        + item.getNamaAddon()
        );
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}