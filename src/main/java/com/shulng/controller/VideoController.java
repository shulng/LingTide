package com.shulng.controller;

import com.shulng.common.PageResult;
import com.shulng.common.Result;
import com.shulng.dto.request.CreateVideoRequest;
import com.shulng.dto.request.PageQueryRequest;
import com.shulng.dto.response.FavoriteResponse;
import com.shulng.dto.response.VideoResponse;
import com.shulng.security.SecurityUtils;
import com.shulng.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private final VideoService videoService;
    private final SecurityUtils securityUtils;

    public VideoController(VideoService videoService, SecurityUtils securityUtils) {
        this.videoService = videoService;
        this.securityUtils = securityUtils;
    }

    @PostMapping("/upload")
    public Result<VideoResponse> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "categoryId", required = false) Long categoryId) {
        Long userId = securityUtils.getCurrentUserId();
        CreateVideoRequest request = new CreateVideoRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setCategoryId(categoryId);
        VideoResponse response = videoService.uploadVideo(userId, file, request);
        return Result.success("上传成功", response);
    }

    @GetMapping("/public/list")
    public Result<PageResult<VideoResponse>> getPublicVideos(PageQueryRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        PageResult<VideoResponse> response = videoService.getPublicVideos(request, userId);
        return Result.success(response);
    }

    @GetMapping("/public/recommended")
    public Result<List<VideoResponse>> getRecommendedVideos() {
        List<VideoResponse> response = videoService.getRecommendedVideos();
        return Result.success(response);
    }

    @GetMapping("/public/latest")
    public Result<List<VideoResponse>> getLatestVideos() {
        List<VideoResponse> response = videoService.getLatestVideos();
        return Result.success(response);
    }

    @GetMapping("/{id}")
    public Result<VideoResponse> getVideoById(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        VideoResponse response = videoService.getVideoById(id, userId);
        return Result.success(response);
    }

    @GetMapping("/user/{userId}")
    public Result<PageResult<VideoResponse>> getUserVideos(@PathVariable Long userId, PageQueryRequest request) {
        PageResult<VideoResponse> response = videoService.getUserVideos(userId, request);
        return Result.success(response);
    }

    @GetMapping("/my")
    public Result<PageResult<VideoResponse>> getMyVideos(PageQueryRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        PageResult<VideoResponse> response = videoService.getCurrentUserVideos(userId, request);
        return Result.success(response);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteVideo(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        videoService.deleteVideo(id, userId);
        return Result.success("删除成功", null);
    }

    @PostMapping("/{id}/like")
    public Result<Boolean> toggleLike(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        boolean liked = videoService.toggleLike(id, userId);
        return Result.success(liked ? "已点赞" : "已取消点赞", liked);
    }

    @PostMapping("/{id}/favorite")
    public Result<Boolean> toggleFavorite(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        boolean favorited = videoService.toggleFavorite(id, userId);
        return Result.success(favorited ? "已收藏" : "已取消收藏", favorited);
    }

    @GetMapping("/favorites")
    public Result<PageResult<FavoriteResponse>> getMyFavorites(PageQueryRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        PageResult<FavoriteResponse> response = videoService.getUserFavorites(userId, request);
        return Result.success(response);
    }

    @GetMapping("/stream/{id}")
    public ResponseEntity<org.springframework.core.io.Resource> streamVideo(
            @PathVariable Long id,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {
        try {
            var videoOpt = videoService.getVideoFilePath(id);
            if (videoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Path videoPath = videoOpt.get();
            File file = videoPath.toFile();
            
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            long fileSize = file.length();
            String contentType = Files.probeContentType(videoPath);
            if (contentType == null) {
                contentType = "video/mp4";
            }

            long startByte = 0;
            long endByte = fileSize - 1;

            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String[] ranges = rangeHeader.substring(6).split("-");
                startByte = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    endByte = Long.parseLong(ranges[1]);
                }
                if (endByte >= fileSize) {
                    endByte = fileSize - 1;
                }
            }

            long contentLength = endByte - startByte + 1;

            if (rangeHeader != null) {
                long finalStartByte = startByte;
                long finalContentLength = contentLength;
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                        .header(HttpHeaders.CONTENT_RANGE, 
                                String.format("bytes %d-%d/%d", startByte, endByte, fileSize))
                        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                        .body(new org.springframework.core.io.AbstractResource() {
                            @Override
                            public String getDescription() {
                                return "Video file: " + file.getName();
                            }
                            @Override
                            public java.io.InputStream getInputStream() throws java.io.IOException {
                                RandomAccessFile raf = new RandomAccessFile(file, "r");
                                raf.seek(finalStartByte);
                                return new java.io.BufferedInputStream(new java.io.InputStream() {
                                    private long remaining = finalContentLength;
                                    @Override
                                    public int read() throws java.io.IOException {
                                        if (remaining <= 0) return -1;
                                        remaining--;
                                        return raf.read();
                                    }
                                    @Override
                                    public int read(byte[] b, int off, int len) throws java.io.IOException {
                                        if (remaining <= 0) return -1;
                                        int toRead = (int) Math.min(len, remaining);
                                        int read = raf.read(b, off, toRead);
                                        if (read > 0) remaining -= read;
                                        return read;
                                    }
                                    @Override
                                    public void close() throws java.io.IOException {
                                        raf.close();
                                    }
                                });
                            }
                        });
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize))
                        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                        .body(new org.springframework.core.io.AbstractResource() {
                            @Override
                            public String getDescription() {
                                return "Video file: " + file.getName();
                            }
                            @Override
                            public java.io.InputStream getInputStream() throws java.io.IOException {
                                return new java.io.FileInputStream(file);
                            }
                        });
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
