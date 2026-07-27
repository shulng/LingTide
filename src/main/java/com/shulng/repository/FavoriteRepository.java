package com.shulng.repository;

import com.shulng.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserIdAndVideoId(Long userId, Long videoId);
    boolean existsByUserIdAndVideoId(Long userId, Long videoId);
    long countByVideoId(Long videoId);
    void deleteByUserIdAndVideoId(Long userId, Long videoId);
    Page<Favorite> findByUserId(Long userId, Pageable pageable);
}
