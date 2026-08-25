package com.amin.jobportal.service;

import com.amin.jobportal.dto.request.CreateJobRequest;
import com.amin.jobportal.dto.request.JobSearchRequest;
import com.amin.jobportal.dto.request.UpdateJobRequest;
import com.amin.jobportal.dto.response.JobResponse;
import com.amin.jobportal.dto.response.JobSummaryResponse;
import com.amin.jobportal.entity.Job;
import com.amin.jobportal.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.w3c.dom.stylesheets.LinkStyle;

import java.awt.*;
import java.util.List;

public interface JobService {
    JobResponse create(CreateJobRequest request, User user);

    JobResponse update(Long id, UpdateJobRequest request, User user);

    void delete(Long jobId, User user);

    JobResponse getById(Long jobId);


    Page<JobSummaryResponse> search(JobSearchRequest request, Pageable pageable);

    List<JobSummaryResponse> getCompanyJobs(Long companyId);
}
