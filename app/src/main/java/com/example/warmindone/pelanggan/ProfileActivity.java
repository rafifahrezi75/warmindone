package com.example.warmindone.pelanggan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.warmindone.LoginActivity;
import com.example.warmindone.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity
        extends AppCompatActivity {

    private TextView tvInitial;
    private TextView tvNama;
    private TextView tvEmail;
    private TextView tvRole;

    private ImageButton btnBack;
    private MaterialButton btnLogout;

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(
                R.layout.activity_profile
        );

        tvInitial =
                findViewById(R.id.tvInitial);

        tvNama =
                findViewById(R.id.tvNama);

        tvEmail =
                findViewById(R.id.tvEmail);

        tvRole =
                findViewById(R.id.tvRole);

        btnBack =
                findViewById(R.id.btnBack);

        btnLogout =
                findViewById(R.id.btnLogout);

        btnBack.setOnClickListener(
                v -> finish()
        );

        btnLogout.setOnClickListener(v -> {

            FirebaseAuth
                    .getInstance()
                    .signOut();

            Intent intent =
                    new Intent(
                            ProfileActivity.this,
                            LoginActivity.class
                    );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });

        loadProfile();
    }

    private void loadProfile() {

        String uid =
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser()
                        .getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if(!doc.exists()) return;

                    String nama =
                            doc.getString("nama");

                    String email =
                            doc.getString("email");

                    String role =
                            doc.getString("role");

                    tvNama.setText(nama);
                    tvEmail.setText(email);
                    tvRole.setText(role);

                    if(nama != null &&
                            !nama.isEmpty()) {

                        tvInitial.setText(
                                nama.substring(
                                        0,
                                        1
                                ).toUpperCase()
                        );
                    }
                });
    }
}