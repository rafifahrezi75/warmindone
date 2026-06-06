package com.example.warmindone.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;
import com.example.warmindone.addonuser.AddonUserModel;

import java.util.ArrayList;

public class CartAddonAdapter
        extends RecyclerView.Adapter<CartAddonAdapter.ViewHolder> {

    private final ArrayList<AddonUserModel> list;

    public CartAddonAdapter(
            ArrayList<AddonUserModel> list) {

        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

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
            int position) {

        AddonUserModel addon =
                list.get(position);

        holder.tvItemName.setText(
                "• " + addon.getNama_addon()
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvItemName;

        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvItemName =
                    itemView.findViewById(
                            R.id.tvItemName
                    );
        }
    }
}