package com.javadbappsproject.computerstore.repository;

import com.javadbappsproject.computerstore.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    public Admin findByUsername(String username);
}
