package com.shulng.dto.response;

import com.shulng.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {
    private Long id;
    private String content;
    private Long videoId;
    private Long userId;
    private String username;
    private String userAvatar;
    private Long parentId;
    private Integer likes;
    private Boolean liked;
    private LocalDateTime createTime;
    private List<CommentResponse> children;

    public CommentResponse() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public List<CommentResponse> getChildren() {
        return children;
    }

    public void setChildren(List<CommentResponse> children) {
        this.children = children;
    }

    public static CommentResponse fromEntity(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setVideoId(comment.getVideoId());
        response.setUserId(comment.getUserId());
        response.setParentId(comment.getParentId());
        response.setLikes(comment.getLikes());
        response.setCreateTime(comment.getCreateTime());
        return response;
    }
}