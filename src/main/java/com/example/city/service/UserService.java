package com.example.city.service;

import com.example.city.model.dto.request.ChangePasswordRequest;
import com.example.city.model.dto.request.ChangeUserRoleRequest;
import com.example.city.model.dto.request.EditAccountRequest;
import com.example.city.model.dto.request.RegisterRequest;
import com.example.city.model.dto.response.UserResponse;
import com.example.city.model.entity.User;

import java.util.List;

public interface UserService {
    User registerUser(RegisterRequest registerRequest);

    User findByUsername(String username);

    User editAccount(String username, EditAccountRequest editAccountRequest);

    boolean changePassword(String username, ChangePasswordRequest changePasswordRequest);

    void changeUserRole(ChangeUserRoleRequest changeUserRoleRequest);

    List<UserResponse> getAllUsers();
}