package com.javadbappsproject.computerstore.repository;

import com.javadbappsproject.computerstore.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findBySlug(String slug);

    Product findBySlugAndIdNot(String slug, Long id);

    Page<Product> findAll(Pageable pageable);

    Page<Product> findAllByNameContaining(String name, Pageable pageable);

    long countAllByNameContaining(String name);
    List<Product> findAllBycategoryId(String categoryId, Pageable pageable);

    long countBycategoryId(String categoryId);


}
