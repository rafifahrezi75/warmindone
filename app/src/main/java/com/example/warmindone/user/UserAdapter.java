package com.example.warmindone.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;

import java.util.List;

public class UserAdapter
        extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final List<UserModel> list;

    public UserAdapter(List<UserModel> list) {
        this.list = list;
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
                                R.layout.user_widget,
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

        UserModel model =
                list.get(position);

        holder.tvNama.setText(
                model.getNama()
        );

        holder.tvEmail.setText(
                model.getEmail()
        );

        holder.tvRole.setText(
                model.getRole()
        );

        holder.tvNoTelp.setText(
                model.getNo_telp()
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvNama;
        TextView tvEmail;
        TextView tvRole;
        TextView tvNoTelp;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNama =
                    itemView.findViewById(
                            R.id.tvNama
                    );

            tvEmail =
                    itemView.findViewById(
                            R.id.tvEmail
                    );

            tvRole =
                    itemView.findViewById(
                            R.id.tvRole
                    );

            tvNoTelp =
                    itemView.findViewById(
                            R.id.tvNoTelp
                    );
        }
    }
}