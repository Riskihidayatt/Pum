package com.example.pum.course;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.MediaController;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pum.R;

public class CourseDetailActivity extends AppCompatActivity {

    private TextView courseTitle;
    private VideoView videoView;
    private String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        // Inisialisasi komponen
        courseTitle = findViewById(R.id.courseTitle);
        videoView = findViewById(R.id.videoView);

        // Terima data dari intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("COURSE_TITLE");
        videoUrl = intent.getStringExtra("COURSE_VIDEO_URL");

        // Set judul kursus
        courseTitle.setText(title);

        // Set video URL ke VideoView
        if (videoUrl != null) {
            Uri uri = Uri.parse(videoUrl);
            videoView.setVideoURI(uri);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
            videoView.start();
        }
    }
}
