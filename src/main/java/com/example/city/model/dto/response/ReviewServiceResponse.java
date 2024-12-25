package com.example.city.model.dto.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewServiceResponse {
   private Long id;

   private Long userId;

   private String userName;

   private String userFullName;

   private Byte rating;

   private String comment;

   private Instant createdAt;
}
