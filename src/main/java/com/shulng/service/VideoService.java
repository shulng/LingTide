package com.shulng.service;

import com.shulng.common.BusinessException;
import com.shulng.common.PageResult;
import com.shulng.dto.request.CreateVideoRequest;
import com.shulng.dto.request.PageQueryRequest;
import com.shulng.dto.response.FavoriteResponse;
import com.shulng.dto.response.UserResponse;
import com.shulng.dto.response.VideoResponse;
import com.shulng.entity.Favorite;
import com.shulng.entity.User;
import com.shulng.entity.Video;
import com.shulng.entity.Category;
import com.shulng.repository.UserRepository;
import com.shulng.repository.VideoRepository;
import com.shulng.repository.CategoryRepository;
import com.shulng.repository.LikeRepository;
import com.shulng.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VideoService {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.cover-path}")
    private String coverPath;

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LikeRepository likeRepository;
    private final FavoriteRepository favoriteRepository;

    public VideoService(VideoRepository videoRepository, 
                        UserRepository userRepository, 
                        CategoryRepository categoryRepository,
                        LikeRepository likeRepository,
                        FavoriteRepository favoriteRepository) {
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.likeRepository = likeRepository;
        this.favoriteRepository = favoriteRepository;
    }

    public VideoResponse uploadVideo(Long userId, MultipartFile file, CreateVideoRequest request) {
        try {
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                    : "";
            String fileName = UUID.randomUUID().toString() + (ext.isEmpty() ? "" : ext);
            Path filePath = uploadDir.resolve(fileName);
            
            Files.copy(file.getInputStream(), filePath);

            Video video = new Video();
            video.setTitle(request.getTitle());
            video.setDescription(request.getDescription());
            video.setFilePath(filePath.toString());
            video.setFileSize(file.getSize());
            video.setFormat(ext.replace(".", "").toUpperCase());
            video.setUserId(userId);
            
            if (request.getCategoryId() != null) {
                video.setCategoryId(request.getCategoryId());
            } else {
                video.setCategoryId(1L);
            }
            
            video.setStatus(Video.VideoStatus.PUBLISHED);
            video.setViews(0);
            video.setLikes(0);
            video.setComments(0);
            video.setFavorites(0);

            Video saved = videoRepository.save(video);
            return convertToResponse(saved, userId);
        } catch (IOException e) {
            throw new BusinessException(500, "视频上传失败: " + e.getMessage());
        }
    }

    public PageResult<VideoResponse> getPublicVideos(PageQueryRequest request, Long currentUserId) {
        Pageable pageable = createPageable(request);
        Page<Video> page;

        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            if (request.getCategoryId() != null) {
                page = videoRepository.searchByKeywordAndCategory(
                        request.getKeyword(), request.getCategoryId(), 
                        Video.VideoStatus.PUBLISHED, pageable);
            } else {
                page = videoRepository.searchByKeyword(
                        request.getKeyword(), Video.VideoStatus.PUBLISHED, pageable);
            }
        } else if (request.getCategoryId() != null) {
            page = videoRepository.findByCategoryIdAndStatus(
                    request.getCategoryId(), Video.VideoStatus.PUBLISHED, pageable);
        } else {
            page = videoRepository.findByStatus(Video.VideoStatus.PUBLISHED, pageable);
        }

        List<VideoResponse> responses = page.getContent().stream()
                .map(v -> convertToResponse(v, currentUserId))
                .collect(Collectors.toList());

        return PageResult.of(responses, page.getTotalElements(), request.getCurrent(), request.getSize());
    }

    public VideoResponse getVideoById(Long id, Long currentUserId) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "视频不存在"));

        if (video.getStatus() != Video.VideoStatus.PUBLISHED) {
            throw new BusinessException(404, "视频不存在或已被删除");
        }

        video.setViews(video.getViews() + 1);
        videoRepository.save(video);

        return convertToResponse(video, currentUserId);
    }

    public PageResult<VideoResponse> getUserVideos(Long userId, PageQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getCurrent() - 1, request.getSize(), Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Video> page = videoRepository.findByUserId(userId, pageable);

        List<VideoResponse> responses = page.getContent().stream()
                .map(v -> convertToResponse(v, null))
                .collect(Collectors.toList());

        return PageResult.of(responses, page.getTotalElements(), request.getCurrent(), request.getSize());
    }

    public PageResult<VideoResponse> getCurrentUserVideos(Long userId, PageQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getCurrent() - 1, request.getSize(), Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Video> page = videoRepository.findByUserIdAndStatus(userId, Video.VideoStatus.PUBLISHED, pageable);

        List<VideoResponse> responses = page.getContent().stream()
                .map(v -> convertToResponse(v, userId))
                .collect(Collectors.toList());

        return PageResult.of(responses, page.getTotalElements(), request.getCurrent(), request.getSize());
    }

    public List<VideoResponse> getRecommendedVideos() {
        List<Video> videos = videoRepository.findTop20ByStatusOrderByViewsDesc(Video.VideoStatus.PUBLISHED);
        return videos.stream()
                .map(v -> convertToResponse(v, null))
                .collect(Collectors.toList());
    }

    public List<VideoResponse> getLatestVideos() {
        List<Video> videos = videoRepository.findTop20ByStatusOrderByCreateTimeDesc(Video.VideoStatus.PUBLISHED);
        return videos.stream()
                .map(v -> convertToResponse(v, null))
                .collect(Collectors.toList());
    }

    public void deleteVideo(Long id, Long userId) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "视频不存在"));

        if (!video.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权删除该视频");
        }

        try {
            Path filePath = Paths.get(video.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            // Ignore delete error
        }

        video.setStatus(Video.VideoStatus.DELETED);
        videoRepository.save(video);
    }

    public boolean toggleLike(Long videoId, Long userId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new BusinessException(404, "视频不存在"));

        if (likeRepository.existsByUserIdAndVideoId(userId, videoId)) {
            likeRepository.deleteByUserIdAndVideoId(userId, videoId);
            video.setLikes(Math.max(0, video.getLikes() - 1));
            videoRepository.save(video);
            return false;
        } else {
            com.shulng.entity.Like like = new com.shulng.entity.Like();
            like.setUserId(userId);
            like.setVideoId(videoId);
            likeRepository.save(like);
            video.setLikes(video.getLikes() + 1);
            videoRepository.save(video);
            return true;
        }
    }

    public boolean toggleFavorite(Long videoId, Long userId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new BusinessException(404, "视频不存在"));

        if (favoriteRepository.existsByUserIdAndVideoId(userId, videoId)) {
            favoriteRepository.deleteByUserIdAndVideoId(userId, videoId);
            video.setFavorites(Math.max(0, video.getFavorites() - 1));
            videoRepository.save(video);
            return false;
        } else {
            com.shulng.entity.Favorite favorite = new com.shulng.entity.Favorite();
            favorite.setUserId(userId);
            favorite.setVideoId(videoId);
            favoriteRepository.save(favorite);
            video.setFavorites(video.getFavorites() + 1);
            videoRepository.save(video);
            return true;
        }
    }

    public Optional<Path> getVideoFilePath(Long id) {
        return videoRepository.findById(id)
                .filter(v -> v.getStatus() == Video.VideoStatus.PUBLISHED)
                .map(v -> Paths.get(v.getFilePath()));
    }

    public PageResult<FavoriteResponse> getUserFavorites(Long userId, PageQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getCurrent() - 1, request.getSize(), Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Favorite> favorites = favoriteRepository.findByUserId(userId, pageable);

        List<FavoriteResponse> responses = favorites.getContent().stream()
                .map(fav -> {
                    FavoriteResponse resp = new FavoriteResponse();
                    resp.setId(fav.getId());
                    resp.setVideoId(fav.getVideoId());
                    resp.setUserId(fav.getUserId());
                    resp.setCreateTime(fav.getCreateTime());

                    videoRepository.findById(fav.getVideoId()).ifPresent(video -> {
                        resp.setVideoTitle(video.getTitle());
                        resp.setVideoCoverPath(video.getCoverPath());
                        resp.setVideoViews(video.getViews());
                        resp.setVideoLikes(video.getLikes());

                        userRepository.findById(video.getUserId()).ifPresent(user -> {
                            resp.setUsername(user.getNickname() != null ? user.getNickname() : user.getUsername());
                        });
                    });

                    return resp;
                })
                .collect(Collectors.toList());

        return PageResult.of(responses, favorites.getTotalElements(), request.getCurrent(), request.getSize());
    }

    private VideoResponse convertToResponse(Video video, Long currentUserId) {
        VideoResponse response = VideoResponse.fromEntity(video);
        
        userRepository.findById(video.getUserId()).ifPresent(user -> {
            response.setUsername(user.getNickname() != null ? user.getNickname() : user.getUsername());
            response.setUserAvatar(user.getAvatar());
        });

        categoryRepository.findById(video.getCategoryId()).ifPresent(cat -> {
            response.setCategoryName(cat.getName());
        });

        if (currentUserId != null) {
            response.setLiked(likeRepository.existsByUserIdAndVideoId(currentUserId, video.getId()));
            response.setFavorited(favoriteRepository.existsByUserIdAndVideoId(currentUserId, video.getId()));
        } else {
            response.setLiked(false);
            response.setFavorited(false);
        }

        return response;
    }

    private Pageable createPageable(PageQueryRequest request) {
        Sort sort = switch (request.getSortBy()) {
            case "views" -> Sort.by(Sort.Direction.DESC, "views");
            case "likes" -> Sort.by(Sort.Direction.DESC, "likes");
            case "comments" -> Sort.by(Sort.Direction.DESC, "comments");
            default -> Sort.by(Sort.Direction.DESC, "createTime");
        };
        return PageRequest.of(request.getCurrent() - 1, request.getSize(), sort);
    }
}
