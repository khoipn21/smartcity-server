package com.example.city.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {

   @NotNull(message = "Service category ID is required")
   private byte rating;

   @Size(max = 500, message = "Comment must be less than 500 characters")
   private String comment;
}
