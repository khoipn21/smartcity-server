package com.example.city.service;

import com.example.city.model.dto.request.RegisterRequest;
import com.example.city.model.entity.User;

public interface UserService {
    User registerUser(RegisterRequest registerRequest);

    User findByUsername(String username);
}
