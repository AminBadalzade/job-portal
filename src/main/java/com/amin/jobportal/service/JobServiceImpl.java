package com.amin.jobportal.service;

import com.amin.jobportal.dto.request.CreateJobRequest;
import com.amin.jobportal.dto.request.JobSearchRequest;
import com.amin.jobportal.dto.request.UpdateJobRequest;
import com.amin.jobportal.dto.response.JobResponse;
import com.amin.jobportal.dto.response.JobSummaryResponse;
import com.amin.jobportal.entity.Company;
import com.amin.jobportal.entity.Job;
import com.amin.jobportal.entity.User;
import com.amin.jobportal.enums.Role;
import com.amin.jobportal.exception.ForbiddenException;
import com.amin.jobportal.exception.ResourceNotFoundException;
import com.amin.jobportal.mapper.JobMapper;
import com.amin.jobportal.repository.CompanyRepository;
import com.amin.jobportal.repository.JobRepository;
import com.amin.jobportal.specification.JobSpecs;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobMapper jobMapper;
    private final CompanyRepository companyRepository;


    public JobServiceImpl(JobRepository jobRepository, JobMapper jobMapper, CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.jobMapper = jobMapper;
        this.companyRepository = companyRepository;
    }

    @Transactional
    @Override
    public JobResponse create(CreateJobRequest request, User user) {
        Company company = companyRepository.findById(user.getCompany().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        Job job = jobMapper.toEntity(request);
        job.setCompany(company);

        Job dbJob = jobRepository.save(job);
        return jobMapper.toResponse(dbJob);
    }

    @Transactional
    @Override
    public JobResponse update(Long id, UpdateJobRequest request, User user) {
        Job job = jobRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Job not found with id: " + id));

        if(!job.getCompany().getId().equals(user.getCompany().getId())){
            throw new ForbiddenException("You are not authorized to access other job's details");
        }

        jobMapper.updateFromRequest(request, job);
        return jobMapper.toResponse(job);
    }

    @Transactional
    @Override
    public void delete(Long jobId, User user) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if(user.getRole() == Role.ADMIN){
            jobRepository.delete(job);
            return;
        } else if(!user.getCompany().getId().equals(job.getCompany().getId()) ) {
            throw new ForbiddenException("You are not authorized to delete job");
        }
        jobRepository.delete(job);
    }

    @Override
    public JobResponse getById(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        return jobMapper.toResponse(job);
    }

    private Specification<Job> buildSpec(JobSearchRequest jobSearchRequest) {
        Specification<Job> spec = Specification.unrestricted();

        if(jobSearchRequest.getTitle() != null){
            spec = spec.and(JobSpecs.containsTitle(jobSearchRequest.getTitle()));
        }

        if(jobSearchRequest.getCity() != null){
            spec = spec.and(JobSpecs.hasCity(jobSearchRequest.getCity()));
        }

        if(jobSearchRequest.getCountry() != null){
            spec = spec.and(JobSpecs.hasCountry(jobSearchRequest.getCountry()));
        }

        if(jobSearchRequest.getExperienceLevel() != null){
            spec = spec.and(JobSpecs.hasExperienceLevel(jobSearchRequest.getExperienceLevel()));
        }

        if(jobSearchRequest.getEmploymentType() != null){
            spec = spec.and(JobSpecs.hasEmploymentType(jobSearchRequest.getEmploymentType()));
        }

        if(jobSearchRequest.getWorkType() != null){
            spec = spec.and(JobSpecs.hasWorkType(jobSearchRequest.getWorkType()));
        }

        if(jobSearchRequest.getSalaryMin() != null){
            spec = spec.and(JobSpecs.hasSalaryMin(jobSearchRequest.getSalaryMin()));
        }

        if(jobSearchRequest.getSalaryMax() != null){
            spec = spec.and(JobSpecs.hasSalaryMax(jobSearchRequest.getSalaryMax()));
        }

        return spec;
    }


    @Override
    public Page<JobSummaryResponse> search(JobSearchRequest request, Pageable pageable) {
        Specification<Job> jobSpecification = buildSpec(request);
        Page<Job> jobs = jobRepository.findAll(jobSpecification, pageable);
        return jobs.map(jobMapper::toSummaryResponse);
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
