package com.amin.jobportal.controller;

import com.amin.jobportal.dto.response.CompanyJoinRequestResponse;
import com.amin.jobportal.entity.CompanyJoinRequest;
import com.amin.jobportal.entity.User;
import com.amin.jobportal.service.JoinRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class JoinRequestController {

   private final JoinRequestService joinRequestService;

    public JoinRequestController(JoinRequestService joinRequestService) {
        this.joinRequestService = joinRequestService;
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @GetMapping("/{companyId}/join-requests")
    public ResponseEntity<List<CompanyJoinRequestResponse>> getRequests(@PathVariable Long companyId,
                                                                        @AuthenticationPrincipal User user){
     List<CompanyJoinRequestResponse> responseList = joinRequestService.getAllRequests(companyId, user);
     return ResponseEntity.ok(responseList);
    }

   @PreAuthorize("hasRole('EMPLOYER')")
    @PostMapping("/{companyId}/join")
    public ResponseEntity<CompanyJoinRequestResponse> joinCompany(@PathVariable Long companyId, @AuthenticationPrincipal User user){
     CompanyJoinRequestResponse companyJoinRequestResponse = joinRequestService.joinCompany(companyId, user);
     return new ResponseEntity<>(companyJoinRequestResponse, HttpStatus.CREATED);
    }


    @PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/{companyId}/join-requests/{requestId}/approve")
    public ResponseEntity<CompanyJoinRequestResponse> approveRequest(@PathVariable Long companyId, @PathVariable Long requestId, @AuthenticationPrincipal User user){
     CompanyJoinRequestResponse response = joinRequestService.approveRequest(companyId, requestId, user);
     return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/{companyId}/join-requests/{requestId}/reject")
    public ResponseEntity<CompanyJoinRequestResponse> rejectRequest(@PathVariable Long companyId, @PathVariable Long requestId,
                                                                         @AuthenticationPrincipal User user){
     CompanyJoinRequestResponse response = joinRequestService.rejectRequest(companyId, requestId, user);
     return ResponseEntity.ok(response);
    }
}
