package com.example.city.service.impl;

import com.example.city.model.dto.request.ChangePasswordRequest;
import com.example.city.model.dto.request.ChangeUserRoleRequest;
import com.example.city.model.dto.request.EditAccountRequest;
import com.example.city.model.dto.request.RegisterRequest;
import com.example.city.model.dto.response.UserResponse;
import com.example.city.model.entity.Role;
import com.example.city.model.entity.User;
import com.example.city.repository.UserRepository;
import com.example.city.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public User registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already taken");
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .role(Role.ROLE_USER) // Assign default role
                .build();

        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User editAccount(String username, EditAccountRequest editAccountRequest) {
        User user = findByUsername(username);

        if (editAccountRequest.getEmail() != null && !editAccountRequest.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(editAccountRequest.getEmail())) {
                throw new RuntimeException("Email is already taken");
            }
            user.setEmail(editAccountRequest.getEmail());
        }

        if (editAccountRequest.getFullName() != null) {
            user.setFullName(editAccountRequest.getFullName());
        }

        user.setUpdatedAt(Instant.now());

        return userRepository.save(user);
    }

    @Override
    public boolean changePassword(String username, ChangePasswordRequest changePasswordRequest) {
        User user = findByUsername(username);

        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            return false; // Current password is incorrect
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        user.setUpdatedAt(Instant.now());

        userRepository.save(user);
        return true;
    }

    @Override
    public void changeUserRole(ChangeUserRoleRequest changeUserRoleRequest) {
        User user = userRepository.findByUsername(changeUserRoleRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + changeUserRoleRequest.getUsername()));

        Role newRole;
        try {
            newRole = Role.valueOf(changeUserRoleRequest.getRole());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + changeUserRoleRequest.getRole());
        }

        user.setRole(newRole);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }
}