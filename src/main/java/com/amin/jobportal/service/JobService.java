package com.amin.jobportal.service;

import com.amin.jobportal.dto.request.CreateJobRequest;
import com.amin.jobportal.dto.request.JobSearchRequest;
import com.amin.jobportal.dto.request.UpdateJobRequest;
import com.amin.jobportal.dto.response.JobResponse;
import com.amin.jobportal.dto.response.JobSummaryResponse;
import org.springframework.data.domain.Page;
import org.w3c.dom.stylesheets.LinkStyle;

import java.awt.*;
import java.util.List;

public interface JobService {
    JobResponse create(CreateJobRequest request);

    JobResponse update(Long id, UpdateJobRequest request);

    void delete(Long id);

    JobResponse getById(Long id);

    Page<JobSummaryResponse> search(JobSearchRequest request);

    List<JobSummaryResponse> getCompanyJobs(Long companyId);
}
