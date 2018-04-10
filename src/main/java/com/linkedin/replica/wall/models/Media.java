package com.linkedin.replica.wall.models;

import java.util.ArrayList;

public class Media {
    private ArrayList<String> images;
    private ArrayList<String> videos;

    public Media(ArrayList<String> images, ArrayList<String> videos) {
        this.images = images;
        this.videos = videos;
    }

    public Media() {
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<String> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<String> videos) {
        this.videos = videos;
    }

    @Override
    public String toString() {
        return "Media{" +
                "images=" + images +
                ", videos=" + videos +
                '}';
    }
}
