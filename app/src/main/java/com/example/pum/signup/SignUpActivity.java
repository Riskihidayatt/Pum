package com.example.pum.signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pum.R;
import com.example.pum.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText signupUsername, signupName, signupEmail, signupPassword;
    private Button signupSubmit;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Inisialisasi view
        signupUsername = findViewById(R.id.signup_username);
        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupSubmit = findViewById(R.id.signup_submit);

        // Inisialisasi FirebaseAuth dan Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signupSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfUsernameAndEmailExist();
            }
        });
    }

    // Cek apakah username dan email sudah ada di database
    private void checkIfUsernameAndEmailExist() {
        String username = signupUsername.getText().toString().trim();
        String email = signupEmail.getText().toString().trim();

        // Cek apakah username dan email kosong
        if (TextUtils.isEmpty(username)) {
            signupUsername.setError("Masukkan username");
            signupUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signupEmail.setError("Masukkan email yang valid");
            signupEmail.requestFocus();
            return;
        }

        // Query untuk memeriksa username dan email
        Query usernameQuery = db.collection("users").whereEqualTo("username", username);
        Query emailQuery = db.collection("users").whereEqualTo("email", email);

        // Cek username
        usernameQuery.get().addOnCompleteListener(usernameTask -> {
            if (usernameTask.isSuccessful() && !usernameTask.getResult().isEmpty()) {
                // Jika username sudah ada, tampilkan pesan
                signupUsername.setError("Username sudah digunakan. Silakan pilih username lain.");
                signupUsername.requestFocus();
            } else {
                // Cek email jika username belum ada
                emailQuery.get().addOnCompleteListener(emailTask -> {
                    if (emailTask.isSuccessful() && !emailTask.getResult().isEmpty()) {
                        // Jika email sudah ada, tampilkan pesan
                        signupEmail.setError("Email sudah terdaftar. Silakan gunakan email lain.");
                        signupEmail.requestFocus();
                    } else {
                        // Jika username dan email belum ada, lanjutkan proses pendaftaran
                        createAccount();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(SignUpActivity.this, "Terjadi kesalahan saat memeriksa email: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(SignUpActivity.this, "Terjadi kesalahan saat memeriksa username: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void createAccount() {
        String email = signupEmail.getText().toString().trim();
        String password = signupPassword.getText().toString().trim();
        String username = signupUsername.getText().toString().trim();
        String name = signupName.getText().toString().trim();

        // Validasi data input
        if (TextUtils.isEmpty(name)) {
            signupName.setError("Masukkan nama");
            signupName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            signupPassword.setError("Password minimal harus 6 karakter");
            signupPassword.requestFocus();
            return;
        }

        // Mendaftarkan pengguna baru di Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserDataToFirestore(user.getUid(), username, name, email);
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Gagal mendaftar: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserDataToFirestore(String userId, String username, String name, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("name", name);
        user.put("email", email);
        user.put("createdAt", FieldValue.serverTimestamp());

        // Simpan data pengguna di koleksi "users" dengan userId sebagai ID dokumen
        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    // Menampilkan pesan sukses dan beralih ke LoginActivity
                    Toast.makeText(SignUpActivity.this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show();
                    // Pindah ke LoginActivity setelah pendaftaran berhasil
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignUpActivity.this, "Gagal menyimpan data pengguna: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}
