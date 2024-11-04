package com.example.pum.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pum.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editName, editEmail;
    private Button saveButton;
    private ImageButton backButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Inisialisasi FirebaseAuth dan Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inisialisasi view
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton); // Inisialisasi back button

        // Isi data saat ini di Firebase
        loadUserData();

        // Simpan perubahan ketika tombol Save diklik
        saveButton.setOnClickListener(v -> saveUserData());

        // Tombol kembali ke halaman ProfileActivity
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Membersihkan tumpukan aktivitas
            startActivity(intent);
            finish(); // Menutup EditProfileActivity
        });
    }

    private void loadUserData() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        editName.setText(name);
                        editEmail.setText(email);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Gagal memuat data pengguna", Toast.LENGTH_SHORT).show());
    }

    private void saveUserData() {
        String name = editName.getText().toString();
        String email = editEmail.getText().toString();
        String userId = mAuth.getCurrentUser().getUid();

        // Simpan data yang diperbarui di Firestore
        db.collection("users").document(userId).update("name", name, "email", email)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfileActivity.this, "Perubahan disimpan", Toast.LENGTH_SHORT).show();

                    // Pindah ke halaman ProfileActivity setelah data disimpan
                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    finish(); // Tutup EditProfileActivity
                })
                .addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Gagal menyimpan perubahan", Toast.LENGTH_SHORT).show());
    }
}
