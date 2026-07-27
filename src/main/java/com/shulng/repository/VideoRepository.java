package com.shulng.repository;

import com.shulng.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    Page<Video> findByStatus(Video.VideoStatus status, Pageable pageable);
    
    Page<Video> findByUserId(Long userId, Pageable pageable);
    
    Page<Video> findByUserIdAndStatus(Long userId, Video.VideoStatus status, Pageable pageable);
    
    Page<Video> findByCategoryIdAndStatus(Long categoryId, Video.VideoStatus status, Pageable pageable);
    
    @Query("SELECT v FROM Video v WHERE v.status = :status AND (LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Video> searchByKeyword(@Param("keyword") String keyword, @Param("status") Video.VideoStatus status, Pageable pageable);
    
    @Query("SELECT v FROM Video v WHERE v.status = :status AND v.categoryId = :categoryId AND (LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Video> searchByKeywordAndCategory(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, @Param("status") Video.VideoStatus status, Pageable pageable);
    
    List<Video> findTop20ByStatusOrderByViewsDesc(Video.VideoStatus status);
    
    List<Video> findTop20ByStatusOrderByCreateTimeDesc(Video.VideoStatus status);
    
    long countByStatus(Video.VideoStatus status);
    
    long countByUserId(Long userId);
    
    long countByCategoryIdAndStatus(Long categoryId, Video.VideoStatus status);
}
