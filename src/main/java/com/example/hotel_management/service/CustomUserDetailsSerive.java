package com.example.hotel_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.hotel_management.exception.OurException;
import com.example.hotel_management.repo.UserRepository;

public class CustomUserDetailsSerive implements UserDetailsService {
    @Autowired
    private UserRepository user_repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws OurException {
        return user_repository.findByEmail(username).orElseThrow(() -> new OurException("Username/Email not found"));
    }
}
