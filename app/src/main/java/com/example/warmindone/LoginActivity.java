package com.example.warmindone;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.warmindone.kasiradmin.DashboardKasirAdmin;
import com.example.warmindone.pelanggan.DashboardUser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword;
    MaterialButton btnLogin;
    TextView txtDaftar;
    ImageView btnBack;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtDaftar = findViewById(R.id.txtMasukWelcome);

        btnBack = findViewById(R.id.bgRegister);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        });

        btnLogin.setOnClickListener(v -> loginUser());

        // Navigasi ke halaman Register
        txtDaftar.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email wajib diisi");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password wajib diisi");
            return;
        }

        // 1. LOGIN KE FIREBASE AUTH
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = mAuth.getCurrentUser().getUid();

                    // 2. AMBIL DATA USER DARI FIRESTORE
                    db.collection("users").document(uid)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String role = documentSnapshot.getString("role");
                                    Toast.makeText(this, "Login berhasil sebagai " + role, Toast.LENGTH_SHORT).show();

                                    if ("admin".equals(role)) {
                                        startActivity(new Intent(this, DashboardKasirAdmin.class));
                                    } else {
                                        startActivity(new Intent(this, DashboardUser.class));
                                    }
                                    finish();
                                } else {
                                    Toast.makeText(this, "Data user tidak ditemukan di database", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Gagal mengambil data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Login gagal: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
