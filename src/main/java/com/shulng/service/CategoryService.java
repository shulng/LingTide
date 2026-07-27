package com.shulng.service;

import com.shulng.common.BusinessException;
import com.shulng.dto.response.CategoryResponse;
import com.shulng.entity.Category;
import com.shulng.entity.Video;
import com.shulng.repository.CategoryRepository;
import com.shulng.repository.VideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final VideoRepository videoRepository;

    public CategoryService(CategoryRepository categoryRepository, VideoRepository videoRepository) {
        this.categoryRepository = categoryRepository;
        this.videoRepository = videoRepository;
    }

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findByStatusOrderBySortOrderAsc(1);
        return categories.stream()
                .map(cat -> {
                    CategoryResponse response = CategoryResponse.fromEntity(cat);
                    long count = videoRepository.countByCategoryIdAndStatus(cat.getId(), Video.VideoStatus.PUBLISHED);
                    response.setVideoCount((int) count);
                    return response;
                })
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "分类不存在"));
        CategoryResponse response = CategoryResponse.fromEntity(category);
        return response;
    }
}
