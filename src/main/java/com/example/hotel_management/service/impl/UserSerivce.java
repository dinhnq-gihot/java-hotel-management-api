package com.example.hotel_management.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.hotel_management.dto.LoginRequest;
import com.example.hotel_management.dto.Response;
import com.example.hotel_management.dto.UserDTO;
import com.example.hotel_management.entity.User;
import com.example.hotel_management.exception.OurException;
import com.example.hotel_management.repo.UserRepository;
import com.example.hotel_management.service.interfac.IUserService;
import com.example.hotel_management.utils.JWTUtils;
import com.example.hotel_management.utils.Utils;


public class UserSerivce implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Response register(User user) {
        Response res = new Response();

        try {
            if (user.getRole() == null || user.getRole().isBlank()) {
                user.setRole("User");
            }
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new OurException(user.getEmail() + " Already exits");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User saveUser = userRepository.save(user);
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(saveUser);

            res.setStatusCode(200);
            res.setUser(userDTO);
            res.setMessage("success");
        } catch (OurException e) {
            res.setStatusCode(400);
            res.setMessage(e.getMessage());
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage(e.getMessage());
        }

        return res;
    }

    @Override
    public Response login(LoginRequest loginRequest) {
        Response res = new Response();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            User user = userRepository.findByEmail(null).orElseThrow(() -> new OurException("User Not Found"));
            String token = jwtUtils.generateToken(user);

            res.setToken(token);
            res.setExpirationTime("7 days");
            res.setRole(user.getRole());
            res.setMessage("successful");
            res.setStatusCode(200);
        } catch (OurException e) {
            res.setStatusCode(400);
            res.setMessage(e.getMessage());
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage(e.getMessage());
        }
        return res;
    }

    @Override
    public Response getAllUsers() {
        Response res = new Response();

        try {
            List<User> userList = userRepository.findAll();
            List<UserDTO> userDTOs = userList.stream().map(Utils::mapUserEntityToUserDTO).collect(Collectors.toList());

            res.setStatusCode(200);
            res.setMessage("success");
            res.setUserList(userDTOs);
        } catch (OurException e) {
            res.setStatusCode(400);
            res.setMessage(e.getMessage());
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage(e.getMessage());
        }
        return res;
    }

    @Override
    public Response getUserBookingHistory(String userId) {
        Response res = new Response();

        try {
            User user = userRepository.findById(Long.valueOf(userId))
                    .orElseThrow(() -> new OurException("User not found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTOPlusUserBookingsAndRoom(user);

            res.setStatusCode(200);
            res.setMessage("success");
            res.setUser(userDTO);

        } catch (OurException e) {
            res.setStatusCode(400);
            res.setMessage(e.getMessage());
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage(e.getMessage());
        }
        return res;
    }

    @Override
    public Response deleteUser(String userId) {
        Response res = new Response();
        try {
            userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new OurException("User Not Found"));
            userRepository.deleteById(Long.valueOf(userId));

            res.setMessage("successful");
            res.setStatusCode(200);
        } catch (OurException e) {
            res.setStatusCode(404);
            res.setMessage(e.getMessage());
        } catch (Exception e) {
            res.setStatusCode(500);
            res.setMessage("Error deleting a user " + e.getMessage());
        }

        return res;
    }

    @Override
    public Response getUserById(String userId) {
        Response response = new Response();

        try {
            User user = userRepository.findById(Long.valueOf(userId))
                    .orElseThrow(() -> new OurException("User Not Found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);

            response.setMessage("success");
            response.setStatusCode(200);
            response.setUser(userDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting a user by id " + e.getMessage());

        }
        return response;
    }

    @Override
    public Response getMyInfo(String email) {
        Response response = new Response();

        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new OurException("User Not Found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);

            response.setMessage("success");
            response.setStatusCode(200);
            response.setUser(userDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting a user info " + e.getMessage());

        }
        return response;
    }

}
