package com.example.city.model.dto.response;

import java.time.Instant;
import java.util.List;

import com.example.city.model.entity.Service;
import com.example.city.model.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitServiceResponse {
   private float averageRating;

   private List<VisitResponse> listVisit;
}
