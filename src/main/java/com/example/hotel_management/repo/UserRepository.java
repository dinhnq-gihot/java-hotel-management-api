package com.example.hotel_management.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hotel_management.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
