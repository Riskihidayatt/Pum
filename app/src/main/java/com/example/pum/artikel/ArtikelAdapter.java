package com.example.pum.artikel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.pum.R;
import java.util.List;

public class ArtikelAdapter extends RecyclerView.Adapter<ArtikelAdapter.ViewHolder> {

    private final Context context;
    private final List<Artikel> artikelList;
    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Artikel artikel);
    }

    public ArtikelAdapter(Context context, List<Artikel> artikelList, OnItemClickListener listener) {
        this.context = context;
        this.artikelList = artikelList;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Artikel artikel = artikelList.get(position);
        holder.articleTitle.setText(artikel.getTitle());
        holder.articleCreator.setText(artikel.getCreator());

        // Menggunakan Glide untuk memuat gambar dari URL Firebase dengan mengabaikan cache
        Glide.with(context)
                .load(artikel.getImageUrl())  // Memuat URL gambar dari Firebase
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Melewati cache disk
                .skipMemoryCache(true) // Melewati cache memori
                .placeholder(R.drawable.code_images) // Placeholder sementara gambar dimuat
                .into(holder.articleImage);

        // Handle klik pada item artikel
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(artikel));
    }

    @Override
    public int getItemCount() {
        return artikelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView articleImage;
        public TextView articleTitle, articleCreator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            articleImage = itemView.findViewById(R.id.articleImage);
            articleTitle = itemView.findViewById(R.id.articleTitle);
            articleCreator = itemView.findViewById(R.id.articleCreator);
        }
    }
}
