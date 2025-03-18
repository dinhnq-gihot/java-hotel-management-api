package com.example.hotel_management.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.CachingUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.example.hotel_management.utils.JWTUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter {
    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private CachingUserDetailsService cachingUserDetailsService;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwtToken = authHeader.substring(7);
        final String userEmail = jwtUtils.extractUsername(jwtToken);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            UserDetails userDetails = cachingUserDetailsService.loadUserByUsername(userEmail);
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userEmail, null,
                    userDetails.getAuthorities());
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            securityContext.setAuthentication(token);
            SecurityContextHolder.setContext(securityContext);
        }
        filterChain.doFilter(request, response);
    }
}
