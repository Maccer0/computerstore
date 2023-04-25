package com.javadbappsproject.computerstore.repository;

import com.javadbappsproject.computerstore.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    public Category findByName(String name);

    public List<Category> findAllByOrderBySortingAsc();

    public Category findBySlug(String slug);
}
