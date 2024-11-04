package com.example.pum.course;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pum.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class TambahCourseActivity extends AppCompatActivity {

    private EditText editTitle, editCreator, editDescription;
    private ImageView courseImagePreview;
    private Button btnChooseThumbnail, btnChooseVideo, btnSaveCourse;

    private Uri videoUri;
    private Uri thumbnailUri;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_course);

        // Hubungkan komponen layout dengan variabel
        editTitle = findViewById(R.id.editTitle);
        editCreator = findViewById(R.id.editCreator);
        editDescription = findViewById(R.id.editDescription);
        courseImagePreview = findViewById(R.id.courseImagePreview);
        btnChooseThumbnail = findViewById(R.id.btnChooseThumbnail);
        btnChooseVideo = findViewById(R.id.btnChooseVideo);
        btnSaveCourse = findViewById(R.id.btnSaveCourse);

        // Inisialisasi Firebase Firestore dan Storage
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("course_uploads");

        // Listener untuk memilih thumbnail
        btnChooseThumbnail.setOnClickListener(v -> openThumbnailChooser());

        // Listener untuk memilih video
        btnChooseVideo.setOnClickListener(v -> openVideoChooser());

        // Listener untuk menyimpan course
        btnSaveCourse.setOnClickListener(v -> uploadVideoAndSaveCourse());
    }

    private void openThumbnailChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar Thumbnail"), 1);
    }

    private void openVideoChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Video"), 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            thumbnailUri = data.getData();
            courseImagePreview.setImageURI(thumbnailUri); // Tampilkan thumbnail
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
        }
    }

    private void uploadVideoAndSaveCourse() {
        String title = editTitle.getText().toString().trim();
        String creator = editCreator.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (title.isEmpty() || creator.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show();
            return;
        }

        if (videoUri != null && thumbnailUri != null) {
            // Upload thumbnail ke root
            StorageReference thumbnailRef = storageReference.child(System.currentTimeMillis() + ".jpg"); // Tanpa folder
            thumbnailRef.putFile(thumbnailUri)
                    .addOnSuccessListener(taskSnapshot -> thumbnailRef.getDownloadUrl().addOnSuccessListener(thumbnailUrl -> {
                        // Upload video setelah thumbnail berhasil diupload
                        StorageReference videoRef = storageReference.child(System.currentTimeMillis() + ".mp4"); // Tanpa folder
                        videoRef.putFile(videoUri)
                                .addOnSuccessListener(videoSnapshot -> videoRef.getDownloadUrl().addOnSuccessListener(videoUrl -> {
                                    saveCourseToFirestore(title, creator, description, videoUrl.toString(), thumbnailUrl.toString());
                                }))
                                .addOnFailureListener(e -> Toast.makeText(TambahCourseActivity.this, "Gagal mengunggah video", Toast.LENGTH_SHORT).show());
                    }))
                    .addOnFailureListener(e -> Toast.makeText(TambahCourseActivity.this, "Gagal mengunggah thumbnail", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Pilih video dan thumbnail terlebih dahulu", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveCourseToFirestore(String title, String creator, String description, String videoUrl, String thumbnailUrl) {
        Map<String, Object> course = new HashMap<>();
        course.put("title", title);
        course.put("creator", creator);
        course.put("description", description);
        course.put("videoUrl", videoUrl);
        course.put("thumbnailUrl", thumbnailUrl);
        course.put("createdAt", System.currentTimeMillis());

        db.collection("videos").add(course)  // Gunakan "videos" sebagai koleksi
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(TambahCourseActivity.this, "Course berhasil disimpan", Toast.LENGTH_SHORT).show();
                    finish(); // Kembali ke CourseActivity setelah berhasil menyimpan
                })
                .addOnFailureListener(e -> Toast.makeText(TambahCourseActivity.this, "Gagal menyimpan course", Toast.LENGTH_SHORT).show());
    }
}
