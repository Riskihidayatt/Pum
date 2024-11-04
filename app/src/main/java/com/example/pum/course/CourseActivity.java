package com.example.pum.course;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pum.R;
import com.example.pum.artikel.ArtikelActivity;
import com.example.pum.home.HomeActivity;
import com.example.pum.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CourseActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCourses;
    private CourseAdapter courseAdapter;
    private List<Course> courseList = new ArrayList<>();
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        recyclerViewCourses = findViewById(R.id.recyclerViewCourses);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewCourses.setLayoutManager(gridLayoutManager);

        courseAdapter = new CourseAdapter(this, courseList, course -> {
            Intent intent = new Intent(CourseActivity.this, CourseDetailActivity.class);
            intent.putExtra("COURSE_TITLE", course.getTitle());
            intent.putExtra("COURSE_CREATOR", course.getCreator());
            intent.putExtra("COURSE_IMAGE_URL", course.getThumbnailUrl());
            intent.putExtra("COURSE_DESCRIPTION", course.getDescription());
            intent.putExtra("COURSE_VIDEO_URL", course.getVideoUrl());
            startActivity(intent);
        });
        recyclerViewCourses.setAdapter(courseAdapter);

        db = FirebaseFirestore.getInstance();

        loadCoursesFromFirestore();

        // Menggunakan if untuk menentukan tindakan pada navigasi
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(CourseActivity.this, HomeActivity.class));
                finish();
                return true;

            } else if (itemId == R.id.nav_courses) {
                // Tidak melakukan apa-apa karena sudah berada di halaman courses
                return true;

            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(CourseActivity.this, ProfileActivity.class));
                finish();
                return true;

            } else if (itemId == R.id.nav_artikel) {
                startActivity(new Intent(CourseActivity.this, ArtikelActivity.class));
                finish();
                return true;

            } else if (itemId == R.id.nav_history) {
                Toast.makeText(CourseActivity.this, "History Selected", Toast.LENGTH_SHORT).show();
                return true;

            } else {
                return false;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_courses);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCoursesFromFirestore();
    }


    private void loadCoursesFromFirestore() {
        db.collection("videos") // Pastikan nama koleksi di Firestore benar
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        courseList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Course course = document.toObject(Course.class);
                            courseList.add(course);
                        }
                        courseAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(CourseActivity.this, "Error loading courses.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}