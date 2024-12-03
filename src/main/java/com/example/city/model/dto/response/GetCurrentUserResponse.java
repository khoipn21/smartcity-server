package com.example.city.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCurrentUserResponse {
    private String username;
    private String email;
    private String fullName;
    private String role;
}