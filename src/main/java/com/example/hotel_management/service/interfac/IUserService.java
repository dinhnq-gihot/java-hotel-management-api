package com.example.hotel_management.service.interfac;

import com.example.hotel_management.dto.LoginRequest;
import com.example.hotel_management.dto.Response;
import com.example.hotel_management.entity.User;

public interface IUserService {
    Response register(User user);

    Response login(LoginRequest loginRequest);

    Response getAllUsers();

    Response getUserBookingHistory(String userId);

    Response deleteUser(String userId);

    Response getUserById(String userId);

    Response getMyInfo(String email);
}
