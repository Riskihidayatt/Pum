package com.example.pum.artikel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pum.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TambahArtikelActivity extends AppCompatActivity {

    private EditText editTitle, editCategory;
    private ImageView imagePreview;
    private Button btnChooseImage, btnSaveArticle, btnAddSection;
    private LinearLayout sectionsContainer;

    private Uri imageUri;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private List<Map<String, String>> sectionsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_artikel);

        // Menghubungkan komponen XML dengan Java
        editTitle = findViewById(R.id.editTitle);
        editCategory = findViewById(R.id.editCategory);
        imagePreview = findViewById(R.id.imagePreview);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSaveArticle = findViewById(R.id.btnSaveArticle);
        btnAddSection = findViewById(R.id.btnAddSection);
        sectionsContainer = findViewById(R.id.sectionsContainer);

        // Inisialisasi Firebase
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("thumbnails");
        mAuth = FirebaseAuth.getInstance();

        // Listener untuk memilih gambar
        btnChooseImage.setOnClickListener(v -> openImageChooser());

        // Listener untuk menyimpan artikel
        btnSaveArticle.setOnClickListener(v -> uploadImageAndSaveArticle());

        // Listener untuk menambah bagian
        btnAddSection.setOnClickListener(v -> addSectionFields());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imagePreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addSectionFields() {
        // Membuat TextInputLayout untuk Judul Bagian
        TextInputLayout titleLayout = new TextInputLayout(this);
        titleLayout.setHint("Judul Bagian");
        titleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextInputEditText titleEditText = new TextInputEditText(this);
        titleLayout.addView(titleEditText);

        // Membuat TextInputLayout untuk Isi Konten
        TextInputLayout contentLayout = new TextInputLayout(this);
        contentLayout.setHint("Isi Konten");
        contentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextInputEditText contentEditText = new TextInputEditText(this);
        contentEditText.setMinLines(3);
        contentLayout.addView(contentEditText);

        // Menambahkan layout ke container
        sectionsContainer.addView(titleLayout);
        sectionsContainer.addView(contentLayout);

        // Simpan data ke dalam Map dan tambahkan ke sectionsList
        Map<String, String> sectionData = new HashMap<>();
        sectionsList.add(sectionData);

        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sectionData.put("sectionTitle", s.toString());
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        contentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sectionData.put("body", s.toString());
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void uploadImageAndSaveArticle() {
        String title = editTitle.getText().toString().trim();
        String category = editCategory.getText().toString().trim();

        if (title.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String creator = getUserDisplayName();
                        saveArticleToFirestore(title, category, uri.toString(), creator);
                    }))
                    .addOnFailureListener(e -> Toast.makeText(TambahArtikelActivity.this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show();
        }
    }

    private String getUserDisplayName() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getDisplayName() != null) {
            return user.getDisplayName();
        } else if (user != null && user.getEmail() != null) {
            return user.getEmail(); // Jika displayName kosong, gunakan email sebagai fallback
        } else {
            return "Anonymous";
        }
    }

    private void saveArticleToFirestore(String title, String category, String imageUrl, String creator) {
        Map<String, Object> article = new HashMap<>();
        article.put("title", title);
        article.put("category", category);
        article.put("thumbnailUrl", imageUrl);
        article.put("createdAt", System.currentTimeMillis());
        article.put("creator", creator);
        article.put("content", sectionsList);

        db.collection("articles").add(article)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(TambahArtikelActivity.this, "Artikel berhasil disimpan", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(TambahArtikelActivity.this, "Gagal menyimpan artikel", Toast.LENGTH_SHORT).show());
    }
}
