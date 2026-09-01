package com.amin.jobportal.service;

import com.amin.jobportal.dto.request.JobApplicationRequest;
import com.amin.jobportal.dto.request.UpdateApplicationStatusRequest;
import com.amin.jobportal.dto.response.JobApplicationCompanyResponse;
import com.amin.jobportal.dto.response.JobApplicationSeekerResponse;
import com.amin.jobportal.entity.User;

import java.util.List;

public interface JobApplicationService {
    JobApplicationSeekerResponse apply(Long jobId, User user, JobApplicationRequest request);

    List<JobApplicationSeekerResponse> getMyApplications(User user);

    List<JobApplicationCompanyResponse> getApplicationsForJob(Long jobId, User user);

    JobApplicationCompanyResponse accept(Long applicationId, User user);

    JobApplicationCompanyResponse reject(Long applicationId, User user);

    JobApplicationCompanyResponse review(Long applicationId, User user);

    JobApplicationCompanyResponse interview(Long applicationId, User user);

    void withdraw(Long applicationId, User user);
}
