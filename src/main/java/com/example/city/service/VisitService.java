package com.example.city.service;

import java.util.List;

import com.example.city.model.dto.response.VisitResponse;
import com.example.city.model.dto.response.VisitServiceResponse;

public interface VisitService {

   void addVisitToService(Long ServiceId);

   public List<VisitResponse> getAllVisits();

   VisitServiceResponse getAllVisitService(Long serviceId);

}
