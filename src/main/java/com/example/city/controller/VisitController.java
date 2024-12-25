package com.example.city.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.city.model.dto.response.VisitResponse;
import com.example.city.model.dto.response.VisitServiceResponse;
import com.example.city.service.VisitService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Visit", description = "APIs for managing visit for service in a city")
public class VisitController {

   private final VisitService visitService;
   
   public VisitController( VisitService visitService) {
      this.visitService = visitService;
   }

   @PostMapping("/services/{serviceId}/visit")
   @PreAuthorize("hasRole('USER')")
   @Operation(summary = "Add a visit to Service", description = "Adds a new visit to a specific service. Only user can perform this action.")
   public ResponseEntity<String> addVisitService(@PathVariable Long serviceId) {
            this.visitService.addVisitToService(serviceId);
      return ResponseEntity.ok("Add visit to service successfully");
   }

   @GetMapping("/visits")
   @PreAuthorize("hasRole('ADMIN')")
   @Operation(summary = "Add a visit to Service", description = "Adds a new visit to a specific service. Only user can perform this action.")
   public ResponseEntity<List<VisitResponse>> getAllVisit() {
      List<VisitResponse> res = this.visitService.getAllVisits();
      return ResponseEntity.ok(res);
   }

   @GetMapping("/services/{serviceId}/visits")
   @PreAuthorize("hasRole('USER')")
   @Operation(summary = "Add a visit to Service", description = "Adds a new visit to a specific service. Only user can perform this action.")
   public ResponseEntity<VisitServiceResponse> getAllVisitService(@PathVariable Long serviceId) {
      VisitServiceResponse res = this.visitService.getAllVisitService(serviceId);
      return ResponseEntity.ok(res);
   }

}
