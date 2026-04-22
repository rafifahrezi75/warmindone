package com.example.warmindone;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.warmindone.kasiradmin.DashboardKasirAdmin;
import com.example.warmindone.user.DashboardUser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword;
    MaterialButton btnLogin;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // init
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> loginUser());
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

        // LOGIN FIREBASE AUTH
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    String uid = mAuth.getCurrentUser().getUid();

                    // AMBIL ROLE DARI FIRESTORE
                    db.collection("users").document(uid)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {

                                if (documentSnapshot.exists()) {
                                    String role = documentSnapshot.getString("role");

                                    Toast.makeText(this, "Login berhasil sebagai " + role, Toast.LENGTH_SHORT).show();

                                    // ARAHKAN BERDASARKAN ROLE
                                    if ("admin".equals(role)) {
                                        startActivity(new Intent(this, DashboardKasirAdmin.class));
                                    } else {
                                        startActivity(new Intent(this, DashboardUser.class));
                                    }

                                    finish();
                                }
                            });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Login gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}