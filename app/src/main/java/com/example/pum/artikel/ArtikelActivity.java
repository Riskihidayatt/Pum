package com.example.pum.artikel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pum.R;
import com.example.pum.course.CourseActivity;
import com.example.pum.home.HomeActivity;
import com.example.pum.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ArtikelActivity extends AppCompatActivity {

    private RecyclerView recyclerViewArtikel;
    private ArtikelAdapter artikelAdapter;
    private List<Artikel> artikelList = new ArrayList<>();
    private FirebaseFirestore db;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artikel);

        // Inisialisasi RecyclerView dan LayoutManager
        recyclerViewArtikel = findViewById(R.id.recyclerViewArtikel);
        recyclerViewArtikel.setLayoutManager(new LinearLayoutManager(this));

        // Inisialisasi adapter dan menghubungkannya dengan RecyclerView
        artikelAdapter = new ArtikelAdapter(this, artikelList, artikel -> {
            // Intent ke ArtikelDetailActivity saat artikel diklik
            Intent intent = new Intent(ArtikelActivity.this, ArtikelDetailActivity.class);
            intent.putExtra("ARTICLE_TITLE", artikel.getTitle());
            intent.putExtra("ARTICLE_CREATOR", artikel.getCreator());
            intent.putExtra("ARTICLE_IMAGE_URL", artikel.getImageUrl());
            intent.putExtra("ARTICLE_CONTENT", (ArrayList) artikel.getContent());
            startActivity(intent);
        });
        recyclerViewArtikel.setAdapter(artikelAdapter);

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();

        // Memuat artikel dari Firestore
        loadArticlesFromFirestore();

        // Inisialisasi BottomNavigationView dan mengatur listener
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_artikel); // Menandai Artikel sebagai item yang terpilih
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Pindah ke HomeActivity
                startActivity(new Intent(ArtikelActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_courses) {
                // Pindah ke CourseActivity
                startActivity(new Intent(ArtikelActivity.this, CourseActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Pindah ke ProfileActivity
                startActivity(new Intent(ArtikelActivity.this, ProfileActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_artikel) {
                // Tetap di ArtikelActivity, tidak perlu tindakan lebih lanjut
                return true;
            } else if (itemId == R.id.nav_history) {
                // Placeholder untuk History, jika ada fitur History
                Toast.makeText(ArtikelActivity.this, "History Selected", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }



    // Fungsi untuk memuat artikel dari Firestore
    private void loadArticlesFromFirestore() {
        db.collection("articles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        artikelList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Artikel artikel = document.toObject(Artikel.class);
                            artikelList.add(artikel);
                        }
                        artikelAdapter.notifyDataSetChanged();
                    } else {
                        Log.w("ArtikelActivity", "Error getting articles.", task.getException());
                    }
                });
    }
}
