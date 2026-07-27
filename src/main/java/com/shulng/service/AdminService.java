package com.shulng.service;

import com.shulng.common.BusinessException;
import com.shulng.common.PageResult;
import com.shulng.dto.request.PageQueryRequest;
import com.shulng.dto.response.CategoryResponse;
import com.shulng.dto.response.UserResponse;
import com.shulng.dto.response.VideoResponse;
import com.shulng.entity.User;
import com.shulng.entity.Video;
import com.shulng.entity.Category;
import com.shulng.repository.UserRepository;
import com.shulng.repository.VideoRepository;
import com.shulng.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final CategoryRepository categoryRepository;
    private final VideoService videoService;

    public AdminService(UserRepository userRepository, 
                        VideoRepository videoRepository, 
                        CategoryRepository categoryRepository,
                        VideoService videoService) {
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.categoryRepository = categoryRepository;
        this.videoService = videoService;
    }

    public PageResult<UserResponse> getAllUsers(PageQueryRequest request) {
        Page<User> page = userRepository.findAll(
                PageRequest.of(request.getCurrent() - 1, request.getSize()));
        List<UserResponse> responses = page.getContent().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
        return PageResult.of(responses, page.getTotalElements(), request.getCurrent(), request.getSize());
    }

    public UserResponse updateUserStatus(Long userId, Integer status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        user.setStatus(status);
        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    public PageResult<VideoResponse> getAllVideos(PageQueryRequest request) {
        Page<Video> page = videoRepository.findAll(
                PageRequest.of(request.getCurrent() - 1, request.getSize()));
        List<VideoResponse> responses = page.getContent().stream()
                .map(v -> VideoResponse.fromEntity(v))
                .collect(Collectors.toList());
        return PageResult.of(responses, page.getTotalElements(), request.getCurrent(), request.getSize());
    }

    public VideoResponse updateVideoStatus(Long videoId, String status) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new BusinessException(404, "视频不存在"));
        video.setStatus(Video.VideoStatus.valueOf(status));
        Video saved = videoRepository.save(video);
        return VideoResponse.fromEntity(saved);
    }

    public CategoryResponse createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setSortOrder(0);
        category.setStatus(1);
        Category saved = categoryRepository.save(category);
        return CategoryResponse.fromEntity(saved);
    }

    public long getVideoCount() {
        return videoRepository.count();
    }

    public long getUserCount() {
        return userRepository.count();
    }

    public long getPublishedVideoCount() {
        return videoRepository.countByStatus(Video.VideoStatus.PUBLISHED);
    }
}
