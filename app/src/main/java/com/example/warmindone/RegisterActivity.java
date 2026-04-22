package com.example.warmindone;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText etEmail, etNoHp, etUsername, etPassword;
    MaterialButton btnRegister;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // init firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // init view
        etEmail = findViewById(R.id.etEmail);
        etNoHp = findViewById(R.id.etNoHp);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String email = etEmail.getText().toString().trim();
        String noHp = etNoHp.getText().toString().trim();
        String nama = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // validasi sederhana
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(noHp)
                || TextUtils.isEmpty(nama) || TextUtils.isEmpty(password)) {

            Toast.makeText(this, "Semua field wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // REGISTER KE FIREBASE AUTH
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        String uid = auth.getCurrentUser().getUid();

                        // SIMPAN KE FIRESTORE
                        Map<String, Object> user = new HashMap<>();
                        user.put("nama", nama);
                        user.put("email", email);
                        user.put("no_telp", noHp);
                        user.put("role", "user"); // default role

                        db.collection("users").document(uid)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Register berhasil!", Toast.LENGTH_SHORT).show();

                                    // TODO: pindah ke login / dashboard
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Gagal simpan data", Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        Toast.makeText(this, "Register gagal: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}