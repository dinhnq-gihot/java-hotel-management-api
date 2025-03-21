package com.example.hotel_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Password is required")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
}
