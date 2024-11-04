package com.example.pum.course;

public class Course {
    private String title;
    private String creator;
    private String thumbnailUrl;
    private String description;
    private String videoUrl;

    // Constructor kosong diperlukan untuk Firebase
    public Course() {}

    public Course(String title, String creator, String thumbnailUrl, String description, String videoUrl) {
        this.title = title;
        this.creator = creator;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
        this.videoUrl = videoUrl;
    }

    // Getter dan Setter
    public String getTitle() { return title; }
    public String getCreator() { return creator; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getDescription() { return description; }
    public String getVideoUrl() { return videoUrl; }

    public void setTitle(String title) { this.title = title; }
    public void setCreator(String creator) { this.creator = creator; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public void setDescription(String description) { this.description = description; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
}


