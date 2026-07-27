package com.shulng.repository;

import com.shulng.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByStatusOrderBySortOrderAsc(Integer status);
}
