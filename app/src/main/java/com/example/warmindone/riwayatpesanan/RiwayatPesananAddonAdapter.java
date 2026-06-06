package com.example.warmindone.riwayatpesanan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;

import java.util.ArrayList;

public class RiwayatPesananAddonAdapter
        extends RecyclerView.Adapter<
        RiwayatPesananAddonAdapter.ViewHolder> {

    private final ArrayList<RiwayatPesananAddonModel>
            list;

    public RiwayatPesananAddonAdapter(
            ArrayList<RiwayatPesananAddonModel> list) {

        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(
                                parent.getContext())
                        .inflate(
                                R.layout.itempesananaddon_widget,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        RiwayatPesananAddonModel addon =
                list.get(position);

        holder.tvAddon.setText(
                addon.getJumlah()
                        + "x "
                        + addon.getNamaAddon()
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvAddon;

        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvAddon =
                    itemView.findViewById(
                            R.id.tvAddon
                    );
        }
    }
}