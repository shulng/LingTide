package com.shulng.dto.response;

public class StatsResponse {
    private long totalUsers;
    private long totalVideos;
    private long publishedVideos;

    public StatsResponse() {}

    public StatsResponse(long totalUsers, long totalVideos, long publishedVideos) {
        this.totalUsers = totalUsers;
        this.totalVideos = totalVideos;
        this.publishedVideos = publishedVideos;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalVideos() {
        return totalVideos;
    }

    public void setTotalVideos(long totalVideos) {
        this.totalVideos = totalVideos;
    }

    public long getPublishedVideos() {
        return publishedVideos;
    }

    public void setPublishedVideos(long publishedVideos) {
        this.publishedVideos = publishedVideos;
    }
}