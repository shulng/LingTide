package com.shulng.dto.response;

import com.shulng.entity.Video;

import java.time.LocalDateTime;

public class VideoResponse {
    private Long id;
    private String title;
    private String description;
    private String coverPath;
    private Long fileSize;
    private Integer duration;
    private String format;
    private Long userId;
    private String username;
    private String userAvatar;
    private Long categoryId;
    private String categoryName;
    private Integer views;
    private Integer likes;
    private Integer comments;
    private Integer favorites;
    private String status;
    private Boolean liked;
    private Boolean favorited;
    private LocalDateTime createTime;

    public VideoResponse() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public Integer getFavorites() {
        return favorites;
    }

    public void setFavorites(Integer favorites) {
        this.favorites = favorites;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public Boolean getFavorited() {
        return favorited;
    }

    public void setFavorited(Boolean favorited) {
        this.favorited = favorited;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public static VideoResponse fromEntity(Video video) {
        VideoResponse response = new VideoResponse();
        response.setId(video.getId());
        response.setTitle(video.getTitle());
        response.setDescription(video.getDescription());
        response.setCoverPath(video.getCoverPath());
        response.setFileSize(video.getFileSize());
        response.setDuration(video.getDuration());
        response.setFormat(video.getFormat());
        response.setUserId(video.getUserId());
        response.setCategoryId(video.getCategoryId());
        response.setViews(video.getViews());
        response.setLikes(video.getLikes());
        response.setComments(video.getComments());
        response.setFavorites(video.getFavorites());
        response.setStatus(video.getStatus().name());
        response.setCreateTime(video.getCreateTime());
        return response;
    }
}