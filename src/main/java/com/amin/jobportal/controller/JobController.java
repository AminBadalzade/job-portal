package com.amin.jobportal.controller;

import com.amin.jobportal.dto.request.CreateJobRequest;
import com.amin.jobportal.dto.request.JobSearchRequest;
import com.amin.jobportal.dto.request.UpdateJobRequest;
import com.amin.jobportal.dto.response.JobApplicationCompanyResponse;
import com.amin.jobportal.dto.response.JobResponse;
import com.amin.jobportal.dto.response.JobSummaryResponse;
import com.amin.jobportal.entity.User;
import com.amin.jobportal.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJob(@PathVariable Long id){
       JobResponse jobResponse = jobService.getById(id);
       return ResponseEntity.ok(jobResponse);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<JobSummaryResponse>> getJob(@RequestBody JobSearchRequest jobSearchRequest){
        Page<JobSummaryResponse> jobs = jobService.search(jobSearchRequest);
        return ResponseEntity.ok(jobs);
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @PostMapping
    public ResponseEntity<JobResponse> createJob(@RequestBody CreateJobRequest createJobRequest, @AuthenticationPrincipal User currentUser){
        return ResponseEntity.ok(jobService.create(createJobRequest, currentUser));
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long id, @RequestBody UpdateJobRequest updateJobRequest, @AuthenticationPrincipal User currentUser){
        return ResponseEntity.ok(jobService.update(id, updateJobRequest, currentUser));
    }

    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId, @AuthenticationPrincipal User currentUser){
        jobService.delete(jobId, currentUser);

        return ResponseEntity.noContent().build();    }


    @PreAuthorize("hasRole('EMPLOYER')")
    @GetMapping("/{jobId}/applicants")
    public ResponseEntity<List<JobApplicationCompanyResponse>> getAllApplicationForJob(@PathVariable Long jobId){
        return null;
    }
}
