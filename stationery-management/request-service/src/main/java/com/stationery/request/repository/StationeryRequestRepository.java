package com.stationery.request.repository;

import com.stationery.request.model.RequestStatus;
import com.stationery.request.model.StationeryRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationeryRequestRepository extends JpaRepository<StationeryRequest, Long> {
    List<StationeryRequest> findByStudentEmail(String studentEmail);
    List<StationeryRequest> findByStatus(RequestStatus status);
    List<StationeryRequest> findByStudentEmailAndStatus(String studentEmail, RequestStatus status);
}