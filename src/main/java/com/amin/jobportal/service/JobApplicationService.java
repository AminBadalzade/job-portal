package com.amin.jobportal.service;

import com.amin.jobportal.dto.request.JobApplicationRequest;
import com.amin.jobportal.dto.request.UpdateApplicationStatusRequest;
import com.amin.jobportal.dto.response.JobApplicationCompanyResponse;
import com.amin.jobportal.dto.response.JobApplicationSeekerResponse;

import java.util.List;

public interface JobApplicationService {
    JobApplicationSeekerResponse apply(JobApplicationRequest request);

    List<JobApplicationSeekerResponse> getMyApplications();

    List<JobApplicationCompanyResponse> getApplicationsForJob(Long jobId);

    JobApplicationCompanyResponse updateStatus(Long id, UpdateApplicationStatusRequest request);

    void withdraw(Long id);
}
