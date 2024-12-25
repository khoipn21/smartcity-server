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
public class VisitResponse {
   private Long id;
   private Long userId;         // Trích xuất ID thay vì trả về toàn bộ User
   private String userName;     // Thêm thông tin cần thiết
   private Long serviceId;      // Trích xuất ID thay vì trả về toàn bộ Service
   private String serviceName;  // Thêm thông tin cần thiết
   private Instant visitDate;
}
