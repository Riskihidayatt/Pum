package com.example.pum.artikel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Artikel implements Serializable {
    private String title;
    private String creator;
    private String imageUrl; // URL gambar dari Firebase Storage
    private List<Map<String, String>> content;

    public Artikel() {
        // Diperlukan oleh Firestore
    }

    public Artikel(String title, String creator, String imageUrl, List<Map<String, String>> content) {
        this.title = title;
        this.creator = creator;
        this.imageUrl = imageUrl;
        this.content = content;
    }

    public String getTitle() { return title; }
    public String getCreator() { return creator; }
    public String getImageUrl() { return imageUrl; }
    public List<Map<String, String>> getContent() { return content; }
}
