package com.javadbappsproject.computerstore.repository;

import com.javadbappsproject.computerstore.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PageRepository extends JpaRepository<Page, Long> {
    public Page findBySlug(String slug);

    public Page findBySlugAndIdNot(String slug, Long id);

    public List<Page> findAllByOrderBySortingAsc();
}
