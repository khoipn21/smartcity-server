package com.example.city.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeUserRoleRequest {
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "ROLE_USER|ROLE_ADMIN", message = "Role must be either ROLE_USER or ROLE_ADMIN")
    private String role;
}