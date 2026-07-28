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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final CategoryRepository categoryRepository;
    private final VideoService videoService;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository, 
                        VideoRepository videoRepository, 
                        CategoryRepository categoryRepository,
                        VideoService videoService,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.categoryRepository = categoryRepository;
        this.videoService = videoService;
        this.passwordEncoder = passwordEncoder;
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

    public CategoryResponse updateCategory(Long id, String name, String description, Integer sortOrder, Integer status) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "分类不存在"));
        if (name != null) {
            category.setName(name);
        }
        if (description != null) {
            category.setDescription(description);
        }
        if (sortOrder != null) {
            category.setSortOrder(sortOrder);
        }
        if (status != null) {
            category.setStatus(status);
        }
        Category saved = categoryRepository.save(category);
        return CategoryResponse.fromEntity(saved);
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new BusinessException(404, "分类不存在");
        }
        categoryRepository.deleteById(id);
    }

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public UserResponse createUser(String username, String password, String email, String nickname, String role) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(400, "用户名已存在");
        }
        if (email != null && userRepository.existsByEmail(email)) {
            throw new BusinessException(400, "邮箱已被注册");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setNickname(nickname != null ? nickname : username);
        user.setRole(User.Role.valueOf(role));
        user.setStatus(1);

        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    public UserResponse deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException(400, "不能删除管理员账号");
        }
        userRepository.deleteById(userId);
        return UserResponse.fromEntity(user);
    }

    public UserResponse updateUserPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        user.setPassword(passwordEncoder.encode(newPassword));
        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    public UserResponse updateUserRole(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        user.setRole(User.Role.valueOf(role));
        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
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
