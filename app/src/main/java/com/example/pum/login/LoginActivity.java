package com.example.pum.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pum.R;
import com.example.pum.home.HomeActivity;
import com.example.pum.signup.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameOrEmail, password;
    private Button loginButton, signupButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Menghubungkan ke view
        usernameOrEmail = findViewById(R.id.usernameOrEmail);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

        // Inisialisasi FirebaseAuth dan Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Fungsi login button
        loginButton.setOnClickListener(v -> loginUser());

        // Fungsi sign-up button
        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Cek jika ada pengguna yang sudah login
        if (mAuth.getCurrentUser() != null) {
            // Jika sudah login, langsung pindah ke HomeActivity
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Tutup LoginActivity agar tidak kembali ke sini setelah HomeActivity
        }
    }

    private void loginUser() {
        String userInput = usernameOrEmail.getText().toString();
        String pass = password.getText().toString();

        // Validasi input email/username dan password
        if (TextUtils.isEmpty(userInput)) {
            usernameOrEmail.setError("Enter email or username");
            usernameOrEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(pass)) {
            password.setError("Enter password");
            password.requestFocus();
            return;
        }

        // Cek apakah input adalah email atau username
        if (Patterns.EMAIL_ADDRESS.matcher(userInput).matches()) {
            // Jika input adalah email, login langsung
            signInWithEmail(userInput, pass);
        } else {
            // Jika input adalah username, cari email terkait di Firestore
            fetchEmailFromUsername(userInput, pass);
        }
    }

    private void signInWithEmail(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login berhasil, pindah ke HomeActivity
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Gagal, tampilkan pesan error
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void fetchEmailFromUsername(String username, String pass) {
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String email = queryDocumentSnapshots.getDocuments().get(0).getString("email");
                        signInWithEmail(email, pass); // Gunakan email yang ditemukan untuk login
                    } else {
                        Toast.makeText(LoginActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
