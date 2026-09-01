package com.amin.jobportal.controller;

import com.amin.jobportal.dto.request.JobApplicationRequest;
import com.amin.jobportal.dto.response.JobApplicationCompanyResponse;
import com.amin.jobportal.dto.response.JobApplicationSeekerResponse;
import com.amin.jobportal.entity.User;
import com.amin.jobportal.service.JobApplicationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @PostMapping("/jobs/{jobId}/applications")
    public ResponseEntity<JobApplicationSeekerResponse> apply(@PathVariable Long jobId,
                                                              @AuthenticationPrincipal User user,
                                                              @RequestBody JobApplicationRequest jobApplicationRequest){
        return new ResponseEntity<>(jobApplicationService.apply(jobId, user, jobApplicationRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @GetMapping("/job-applications")
    public ResponseEntity<List<JobApplicationSeekerResponse>> getMyApplications(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(jobApplicationService.getMyApplications(user));
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<List<JobApplicationCompanyResponse>> getApplicationsOfJob(@PathVariable Long jobId, @AuthenticationPrincipal User user){
        return ResponseEntity.ok(jobApplicationService.getApplicationsForJob(jobId, user));
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/job-applications/{applicationId}/accept")
    public ResponseEntity<JobApplicationCompanyResponse> acceptApplication(@PathVariable Long applicationId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(jobApplicationService.accept(applicationId, user));
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/job-applications/{applicationId}/reject")
    public ResponseEntity<JobApplicationCompanyResponse> rejectApplication(@PathVariable Long applicationId,@AuthenticationPrincipal User user){
        return ResponseEntity.ok(jobApplicationService.reject(applicationId, user));
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/job-applications/{applicationId}/review")
    public ResponseEntity<JobApplicationCompanyResponse> reviewApplication(@PathVariable Long applicationId,@AuthenticationPrincipal User user){
        return ResponseEntity.ok(jobApplicationService.review(applicationId, user));
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/job-applications/{applicationId}/interview")
    public ResponseEntity<JobApplicationCompanyResponse> interviewApplication(@PathVariable Long applicationId,@AuthenticationPrincipal User user){
        return ResponseEntity.ok(jobApplicationService.interview(applicationId, user));
    }

    @PreAuthorize("hasRole('JOB_SEEKER')")
    @DeleteMapping("/job-applications/{id}")
    public ResponseEntity<Void> withdrawApplication(@PathVariable Long id,@AuthenticationPrincipal User user){
        jobApplicationService.withdraw(id, user);
        return ResponseEntity.noContent().build();
    }
}
