package com.example.pum.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pum.R;
import com.example.pum.artikel.ArtikelActivity;
import com.example.pum.artikel.TambahArtikelActivity;
import com.example.pum.course.CourseActivity;
import com.example.pum.course.TambahCourseActivity;
import com.example.pum.home.HomeActivity;
import com.example.pum.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private TextView userName, userEmail;
    private ImageButton backButton;
    private LinearLayout editProfile, logoutOption, addArticle, addVideo;
    private BottomNavigationView bottomNavigation;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize FirebaseAuth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Connect UI components with IDs from the layout
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        backButton = findViewById(R.id.backButton);
        editProfile = findViewById(R.id.editProfile);
        logoutOption = findViewById(R.id.logoutOption);
        addArticle = findViewById(R.id.addArticle);
        addVideo = findViewById(R.id.addVideo);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Load user data in real-time from Firestore
        loadUserData();

        // Back button to return to the previous screen
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        } else {
            Log.e(TAG, "Back button is null");
        }

        // Button for editing profile
        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Button to add an article
        addArticle.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, TambahArtikelActivity.class);
            startActivity(intent);
        });

        // Button to add a course
        addVideo.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, TambahCourseActivity.class);
            startActivity(intent);
        });

        // Logout button
        logoutOption.setOnClickListener(v -> logoutUser());

        // Setup BottomNavigationView for page navigation
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_courses) {
                startActivity(new Intent(ProfileActivity.this, CourseActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_artikel) {
                startActivity(new Intent(ProfileActivity.this, ArtikelActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            } else if (itemId == R.id.nav_history) {
                Toast.makeText(ProfileActivity.this, "History Selected", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        // Set the Profile item in the navbar as selected
        bottomNavigation.setSelectedItemId(R.id.nav_profile);
    }

    // Function to load user data in real-time from Firestore
    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            db.collection("users").document(userId).addSnapshotListener((documentSnapshot, e) -> {
                if (e != null) {
                    Log.e(TAG, "Listen failed.", e);
                    Toast.makeText(ProfileActivity.this, "Failed to listen to data changes", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    String email = documentSnapshot.getString("email");

                    // Update displayed user name and email
                    userName.setText(name);
                    userEmail.setText(email);
                } else {
                    Log.d(TAG, "Document does not exist");
                }
            });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to log out the user
    private void logoutUser() {
        mAuth.signOut();
        Toast.makeText(ProfileActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        finish();
    }
}
