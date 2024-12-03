package com.example.city.controller;

import com.example.city.model.dto.request.ChangePasswordRequest;
import com.example.city.model.dto.request.EditAccountRequest;
import com.example.city.model.dto.request.LoginRequest;
import com.example.city.model.dto.request.RegisterRequest;
import com.example.city.model.dto.response.AuthResponse;
import com.example.city.model.dto.response.GetCurrentUserResponse;
import com.example.city.model.dto.response.LogoutResponse;
import com.example.city.model.entity.User;
import com.example.city.service.UserService;
import com.example.city.service.impl.TokenBlacklistService;
import com.example.city.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          JwtUtil jwtUtil,
                          TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        User user = userService.registerUser(registerRequest);
        String token = jwtUtil.generateToken(user); // User implements UserDetails

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Retrieve the full User entity using the username
        User user = userService.findByUsername(userDetails.getUsername());

        String token = jwtUtil.generateToken(userDetails);
        String username = userDetails.getUsername();
        String email = user.getEmail();
        String fullName = user.getFullName();
        String role = user.getRole().name();

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .username(username)
                .email(email)
                .fullName(fullName)
                .role(role)
                .build();

        return ResponseEntity.ok(authResponse);
    }

    @GetMapping
    public ResponseEntity<GetCurrentUserResponse> getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());

        GetCurrentUserResponse response = GetCurrentUserResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<GetCurrentUserResponse> editAccount(@RequestBody EditAccountRequest editAccountRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User updatedUser = userService.editAccount(userDetails.getUsername(), editAccountRequest);

        GetCurrentUserResponse response = GetCurrentUserResponse.builder()
                .username(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .fullName(updatedUser.getFullName())
                .role(updatedUser.getRole().name())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isChanged = userService.changePassword(userDetails.getUsername(), changePasswordRequest);

        if (isChanged) {
            return ResponseEntity.ok("Password changed successfully.");
        } else {
            return ResponseEntity.badRequest().body("Current password is incorrect.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(HttpServletRequest request) {
        String jwt = extractJwtFromRequest(request);
        if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
            tokenBlacklistService.blacklistToken(jwt);
            SecurityContextHolder.clearContext();
            LogoutResponse logoutResponse = LogoutResponse.builder()
                    .success(true)
                    .build();
            return ResponseEntity.ok(logoutResponse);
        } else {
            LogoutResponse logoutResponse = LogoutResponse.builder()
                    .success(false)
                    .build();
            return ResponseEntity.badRequest().body(logoutResponse);
        }
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}