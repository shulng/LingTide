package com.shulng.repository;

import com.shulng.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByVideoIdAndStatus(Long videoId, Integer status, Pageable pageable);
    List<Comment> findByVideoIdAndStatus(Long videoId, Integer status);
    List<Comment> findByVideoIdAndParentIdIsNullAndStatus(Long videoId, Integer status);
    List<Comment> findByParentIdAndStatus(Long parentId, Integer status);
    long countByVideoIdAndStatus(Long videoId, Integer status);
}
