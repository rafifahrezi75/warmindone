package com.example.warmindone.kasiradmin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warmindone.R;
import com.example.warmindone.user.UserAdapter;
import com.example.warmindone.user.UserModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    private RecyclerView recyclerUser;

    private ArrayList<UserModel> list;
    private UserAdapter adapter;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        recyclerUser =
                findViewById(R.id.recyclerUser);

        recyclerUser.setLayoutManager(
                new LinearLayoutManager(this)
        );

        list = new ArrayList<>();

        adapter = new UserAdapter(list);

        recyclerUser.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadUser();
    }

    private void loadUser() {

        db.collection("users")
                .get()
                .addOnSuccessListener(query -> {

                    list.clear();

                    query.getDocuments()
                            .forEach(doc -> {

                                UserModel model =
                                        new UserModel();

                                model.setId(
                                        doc.getId()
                                );

                                model.setNama(
                                        doc.getString("nama")
                                );

                                model.setEmail(
                                        doc.getString("email")
                                );

                                model.setNo_telp(
                                        doc.getString("no_telp")
                                );

                                model.setRole(
                                        doc.getString("role")
                                );

                                list.add(model);
                            });

                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUser();
    }
}