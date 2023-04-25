package com.javadbappsproject.computerstore.service;

import com.javadbappsproject.computerstore.model.Admin;
import com.javadbappsproject.computerstore.model.User;
import com.javadbappsproject.computerstore.repository.AdminRepository;
import com.javadbappsproject.computerstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private AdminRepository adminRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        Admin admin = adminRepository.findByUsername(username);

        if(adminRepository.findAll().isEmpty())
        {
            Admin admin1 = new Admin();
            admin1.setUsername("admin");
            admin1.setPassword(passwordEncoder().encode("admin"));
            adminRepository.save(admin1);
        }

        if(user != null) {
            return user;
        }

        if(admin != null) {
            return admin;
        }

        throw new UsernameNotFoundException("User: " + username +" not found");
    }
}
