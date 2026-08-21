package com.amin.jobportal.service;

import com.amin.jobportal.dto.request.CreateJobRequest;
import com.amin.jobportal.dto.request.JobSearchRequest;
import com.amin.jobportal.dto.request.UpdateJobRequest;
import com.amin.jobportal.dto.response.JobResponse;
import com.amin.jobportal.dto.response.JobSummaryResponse;
import com.amin.jobportal.entity.Job;
import com.amin.jobportal.mapper.JobMapper;
import com.amin.jobportal.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    public JobServiceImpl(JobRepository jobRepository, JobMapper jobMapper) {
        this.jobRepository = jobRepository;
        this.jobMapper = jobMapper;
    }

    @Override
    public JobResponse create(CreateJobRequest request) {
        return null;
    }

    @Override
    public JobResponse update(Long id, UpdateJobRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public JobResponse getById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));

        return jobMapper.toResponse(job);
    }

    @Override
    public Page<JobSummaryResponse> search(JobSearchRequest request) {
        return null;
    }

    @Override
    public List<JobSummaryResponse> getCompanyJobs(Long companyId) {
        List<Job> jobsByCompany = jobRepository.getJobByCompanyId(companyId);

        if (jobsByCompany.isEmpty()) {
            return Collections.emptyList();
        }

        return jobMapper.toSummaryResponseList(jobsByCompany);
    }
}
