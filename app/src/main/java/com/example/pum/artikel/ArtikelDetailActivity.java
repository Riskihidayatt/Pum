package com.example.pum.artikel;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.pum.R;
import java.util.List;
import java.util.Map;

public class ArtikelDetailActivity extends AppCompatActivity {

    private static final String TAG = "ArtikelDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artikel_detail);

        ImageView articleImageView = findViewById(R.id.articleImageView);
        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView creatorTextView = findViewById(R.id.creatorTextView);
        TextView contentTextView = findViewById(R.id.contentTextView);

        // Mengambil data dari Intent
        String articleTitle = getIntent().getStringExtra("ARTICLE_TITLE");
        String articleCreator = getIntent().getStringExtra("ARTICLE_CREATOR");
        String articleImageUrl = getIntent().getStringExtra("ARTICLE_IMAGE_URL");
        List<Map<String, String>> articleContent = (List<Map<String, String>>) getIntent().getSerializableExtra("ARTICLE_CONTENT");

        // Log untuk memastikan URL gambar benar
        Log.d(TAG, "URL Gambar: " + articleImageUrl);

        // Menampilkan data pada UI
        titleTextView.setText(articleTitle);
        creatorTextView.setText("Creator: " + articleCreator);

        // Menampilkan konten artikel
        StringBuilder contentBuilder = new StringBuilder();
        if (articleContent != null) {
            for (Map<String, String> section : articleContent) {
                String sectionTitle = section.get("sectionTitle");
                String body = section.get("body");
                if (sectionTitle != null) {
                    contentBuilder.append(sectionTitle).append("\n");
                }
                if (body != null) {
                    contentBuilder.append(body).append("\n\n");
                }
            }
        }
        contentTextView.setText(contentBuilder.toString());

        // Menggunakan Glide untuk memuat gambar dari URL
        Glide.with(this)
                .load(articleImageUrl) // URL dari Firebase Storage
                .placeholder(R.drawable.code_images) // Gambar placeholder
                .error(R.drawable.code_images) // Gambar jika gagal memuat
                .into(articleImageView);
    }
}
