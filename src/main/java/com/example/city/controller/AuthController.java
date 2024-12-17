package com.example.city.controller;

import com.example.city.model.dto.request.*;
import com.example.city.model.dto.response.AuthResponse;
import com.example.city.model.dto.response.GetCurrentUserResponse;
import com.example.city.model.dto.response.LogoutResponse;
import com.example.city.model.dto.response.UserResponse;
import com.example.city.model.entity.User;
import com.example.city.service.UserService;
import com.example.city.service.impl.TokenBlacklistService;
import com.example.city.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;


@RestController
@RequestMapping("/api/account")
@Tag(name = "Authentication", description = "APIs for user authentication and account management")
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
    @Operation(summary = "Register a new user", description = "Registers a new user with the provided details.")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
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
    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token.")
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
    @Operation(summary = "Get current user", description = "Retrieves the details of the currently authenticated user.")
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
    @Operation(summary = "Edit account", description = "Edits the current user's account details.")
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
    @Operation(summary = "Change password", description = "Changes the password of the current user.")
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
    @Operation(summary = "Logout user", description = "Logs out the current user by blacklisting their JWT token.")
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

    @PutMapping("/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Change User Role", description = "Allows an admin to change the role of a user.")
    public ResponseEntity<String> changeUserRole(@Valid @RequestBody ChangeUserRoleRequest changeUserRoleRequest) {
        userService.changeUserRole(changeUserRoleRequest);
        return ResponseEntity.ok("User role updated successfully.");
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get All Users", description = "Retrieves a list of all registered users. Accessible only by admins.")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}