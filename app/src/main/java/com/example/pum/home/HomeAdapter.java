package com.example.pum.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.pum.R;
import com.example.pum.course.CourseDetailActivity;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private Context context;
    private List<Home> homeList;

    public HomeAdapter(Context context, List<Home> homeList) {
        this.context = context;
        this.homeList = homeList;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Home home = homeList.get(position);
        holder.titleTextView.setText(home.getTitle());
        holder.creatorTextView.setText(home.getCreator()); // Menampilkan creator
        holder.descriptionTextView.setText(home.getDescription());

        // Load image with Glide
        Glide.with(context)
                .load(home.getThumbnailUrl())
                .placeholder(R.drawable.code_images) // Gambar placeholder
                .into(holder.thumbnailImageView);

        // Handle click event to open detail activity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CourseDetailActivity.class);
            intent.putExtra("COURSE_TITLE", home.getTitle());
            intent.putExtra("COURSE_DESCRIPTION", home.getDescription());
            intent.putExtra("COURSE_THUMBNAIL", home.getThumbnailUrl());
            intent.putExtra("COURSE_VIDEO_URL", home.getVideoUrl()); // Kirim URL video
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return homeList.size();
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, creatorTextView, descriptionTextView;
        ImageView thumbnailImageView;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tvVideoTitle);
            creatorTextView = itemView.findViewById(R.id.tvVideoCreator); // Menambahkan ini
            descriptionTextView = itemView.findViewById(R.id.tvVideoDescription);
            thumbnailImageView = itemView.findViewById(R.id.imgVideoThumbnail);
        }
    }
}
