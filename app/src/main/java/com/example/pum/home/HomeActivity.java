package com.example.pum.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pum.R;
import com.example.pum.artikel.ArtikelActivity;
import com.example.pum.course.CourseActivity;
import com.example.pum.profile.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerViewVideos;
    private HomeAdapter homeAdapter;
    private List<Home> homeList = new ArrayList<>();
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerViewVideos = findViewById(R.id.recyclerViewVideos);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Mendapatkan pengguna yang sedang login
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            loadUserData(userId); // Memanggil fungsi untuk load data user
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }

        recyclerViewVideos.setLayoutManager(new LinearLayoutManager(this));
        homeAdapter = new HomeAdapter(this, homeList);
        recyclerViewVideos.setAdapter(homeAdapter);

        loadRecommendedVideos();

        // Navigasi BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_courses) {
                startActivity(new Intent(HomeActivity.this, CourseActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_artikel) {
                startActivity(new Intent(HomeActivity.this, ArtikelActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_history) {
                Toast.makeText(HomeActivity.this, "History Selected", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });
    }

    // Fungsi untuk memuat data pengguna dari Firestore
    private void loadUserData(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String name = document.getString("name");
                                TextView tvUserName = findViewById(R.id.tvUserName);
                                tvUserName.setText(name);
                            } else {
                                Toast.makeText(HomeActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(HomeActivity.this, "Error getting user data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loadRecommendedVideos() {
        db.collection("videos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        homeList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String creator = document.getString("creator"); // Menambahkan creator
                            String description = document.getString("description");
                            String thumbnailUrl = document.getString("thumbnailUrl");
                            String videoUrl = document.getString("videoUrl");

                            homeList.add(new Home(title, creator, description, thumbnailUrl, videoUrl));
                        }
                        homeAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(HomeActivity.this, "Error loading videos.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
