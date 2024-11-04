package com.example.pum.home;

public class Home {
    private String title;
    private String creator; // Menambahkan creator
    private String description;
    private String thumbnailUrl;
    private String videoUrl; // Menambahkan videoUrl

    // Constructor
    public Home(String title, String creator, String description, String thumbnailUrl, String videoUrl) {
        this.title = title;
        this.creator = creator; // Inisialisasi creator
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUrl = videoUrl; // Inisialisasi videoUrl
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getCreator() {
        return creator; // Getter untuk creator
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getVideoUrl() {
        return videoUrl; // Getter untuk videoUrl
    }
}
