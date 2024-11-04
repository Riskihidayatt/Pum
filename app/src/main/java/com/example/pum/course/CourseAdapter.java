package com.example.pum.course;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.pum.R;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private Context context;
    private List<Course> courseList;
    private OnCourseClickListener onCourseClickListener;

    public CourseAdapter(Context context, List<Course> courseList, OnCourseClickListener listener) {
        this.context = context;
        this.courseList = courseList;
        this.onCourseClickListener = listener; // Inisialisasi listener
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseTitle.setText(course.getTitle());
        holder.courseDescription.setText(course.getDescription()); // Menggunakan ID yang sesuai

        // Load image with Glide
        Glide.with(context)
                .load(course.getThumbnailUrl())
                .placeholder(R.drawable.code_images) // Ganti dengan gambar placeholder yang Anda miliki
                .into(holder.courseImage);

        // Handle click event
        holder.itemView.setOnClickListener(v -> onCourseClickListener.onCourseClick(course));
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView courseImage;
        TextView courseTitle, courseDescription; // Menggunakan ID yang sesuai

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseImage = itemView.findViewById(R.id.courseImage);
            courseTitle = itemView.findViewById(R.id.courseTitle); // ID untuk judul
            courseDescription = itemView.findViewById(R.id.courseDescription); // ID untuk deskripsi
        }
    }

    // Interface untuk meng-handle klik pada kursus
    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }
}
