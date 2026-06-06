package com.example.warmindone.addonuser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.warmindone.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AddonUserAdapter
        extends RecyclerView.Adapter<AddonUserAdapter.ViewHolder> {

    private final ArrayList<AddonUserModel> list;

    public AddonUserAdapter(ArrayList<AddonUserModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.addonuser_widget, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        AddonUserModel addon = list.get(position);

        holder.tvNama.setText(addon.getNama_addon());

        NumberFormat rupiah =
                NumberFormat.getCurrencyInstance(
                        new Locale("id","ID"));

        holder.tvHarga.setText(
                rupiah.format(addon.getHarga())
        );

        Glide.with(holder.itemView.getContext())
                .load(addon.getFotoaddon())
                .placeholder(R.drawable.ic_menu)
                .into(holder.imgAddon);

        holder.cbAddon.setOnCheckedChangeListener(null);

        holder.cbAddon.setChecked(
                addon.isSelected()
        );

        holder.cbAddon.setOnCheckedChangeListener(
                (buttonView, isChecked) ->
                        addon.setSelected(isChecked)
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public ArrayList<AddonUserModel> getSelectedAddons(){

        ArrayList<AddonUserModel> selected =
                new ArrayList<>();

        for(AddonUserModel addon : list){

            if(addon.isSelected()){
                selected.add(addon);
            }
        }

        return selected;
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgAddon;
        TextView tvNama,tvHarga;
        CheckBox cbAddon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAddon =
                    itemView.findViewById(R.id.imgAddon);

            tvNama =
                    itemView.findViewById(R.id.tvNamaAddon);

            tvHarga =
                    itemView.findViewById(R.id.tvHargaAddon);

            cbAddon =
                    itemView.findViewById(R.id.cbAddon);
        }
    }
}