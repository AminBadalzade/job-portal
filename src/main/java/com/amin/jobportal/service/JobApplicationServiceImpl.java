package com.amin.jobportal.service;

import com.amin.jobportal.dto.request.JobApplicationRequest;
import com.amin.jobportal.dto.request.UpdateApplicationStatusRequest;
import com.amin.jobportal.dto.response.JobApplicationCompanyResponse;
import com.amin.jobportal.dto.response.JobApplicationSeekerResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {
    @Override
    public JobApplicationSeekerResponse apply(JobApplicationRequest request) {
        return null;
    }

    @Override
    public List<JobApplicationSeekerResponse> getMyApplications() {
        return List.of();
    }

    @Override
    public List<JobApplicationCompanyResponse> getApplicationsForJob(Long jobId) {
        return List.of();
    }

    @Override
    public JobApplicationCompanyResponse updateStatus(Long id, UpdateApplicationStatusRequest request) {
        return null;
    }

    @Override
    public void withdraw(Long id) {

    }
}
