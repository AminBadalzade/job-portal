package com.amin.jobportal.service;

import com.amin.jobportal.dto.request.JobApplicationRequest;
import com.amin.jobportal.dto.response.JobApplicationCompanyResponse;
import com.amin.jobportal.dto.response.JobApplicationSeekerResponse;
import com.amin.jobportal.entity.Job;
import com.amin.jobportal.entity.JobApplication;
import com.amin.jobportal.entity.Resume;
import com.amin.jobportal.entity.User;
import com.amin.jobportal.enums.ApplicationStatus;
import com.amin.jobportal.exception.ConflictException;
import com.amin.jobportal.exception.ForbiddenException;
import com.amin.jobportal.exception.ResourceNotFoundException;
import com.amin.jobportal.mapper.JobApplicationMapper;
import com.amin.jobportal.repository.JobApplicationRepository;
import com.amin.jobportal.repository.JobRepository;
import com.amin.jobportal.repository.ResumeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final JobApplicationMapper jobApplicationMapper;

    public JobApplicationServiceImpl(JobApplicationRepository jobApplicationRepository, JobRepository jobRepository, ResumeRepository resumeRepository, JobApplicationMapper jobApplicationMapper) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.jobRepository = jobRepository;
        this.resumeRepository = resumeRepository;
        this.jobApplicationMapper = jobApplicationMapper;
    }


    @Override
    public JobApplicationSeekerResponse apply(Long jobId, User user, JobApplicationRequest jobApplicationRequest) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new ResourceNotFoundException("Not found job with id: " + jobId));
        Resume resume = resumeRepository.findById(jobApplicationRequest.getResumeId()).orElseThrow(() -> new ResourceNotFoundException("Not found resume with id: " + jobApplicationRequest.getResumeId()));

        if(!resume.getUser().getId().equals(user.getId())){
            throw new ForbiddenException("You cannot use another user's resume");
        }

        if(jobApplicationRepository.findByJobIdAndUserId(jobId, user.getId()) != null){
            throw new ConflictException("You have already applied to this job");
        }

        JobApplication jobApplication = new JobApplication();
        jobApplication.setJob(job);
        jobApplication.setUser(user);
        jobApplication.setResume(resume);

        JobApplication savedJobApplication = jobApplicationRepository.save(jobApplication);

        return jobApplicationMapper.toSeekerResponse(savedJobApplication);

    }

    @Override
    public List<JobApplicationSeekerResponse> getMyApplications(User user) {
        List<JobApplication> jobApplications = jobApplicationRepository.findJobApplicationsByUserId(user.getId());
        return jobApplications.stream().map(jobApplicationMapper::toSeekerResponse).toList();
    }

    @Override
    public List<JobApplicationCompanyResponse> getApplicationsForJob(Long jobId, User user) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new ResourceNotFoundException("Not found Job with id: " + jobId));

        if (user.getCompany() == null || !job.getCompany().getId().equals(user.getCompany().getId())){
            throw new ForbiddenException("You cannot access applications for another company's job");
        }

        List<JobApplication> jobApplications = jobApplicationRepository.findJobApplicationsByJobId(jobId);


        return jobApplications.stream().map(jobApplicationMapper::toCompanyResponse).toList();
    }

    @Transactional
    @Override
    public JobApplicationCompanyResponse accept(Long applicationId, User user){
        JobApplication jobApplication = jobApplicationRepository.findById(applicationId).orElseThrow(() -> new ResourceNotFoundException("Not found Job application with id: " + applicationId));

        if(user.getCompany() == null || !jobApplication.getJob().getCompany().getId().equals(user.getCompany().getId())) {
            throw new ForbiddenException("You cannot access applications for another company's job");
        }

        if (jobApplication.getStatus() != ApplicationStatus.INTERVIEW) {
            throw new ConflictException("Application can only be accepted after an interview");
        }

        jobApplication.setStatus(ApplicationStatus.ACCEPTED);
        JobApplication savedJobApplication = jobApplicationRepository.save(jobApplication);
        return jobApplicationMapper.toCompanyResponse(savedJobApplication);

    }

    @Transactional
    @Override
    public JobApplicationCompanyResponse reject(Long applicationId, User user){
        JobApplication jobApplication = jobApplicationRepository.findById(applicationId).orElseThrow(() -> new ResourceNotFoundException("Not found Job application with id: " + applicationId));

        if(user.getCompany() == null || !jobApplication.getJob().getCompany().getId().equals(user.getCompany().getId())) {
            throw new ForbiddenException("You cannot access applications for another company's job");
        }

        if (jobApplication.getStatus() == ApplicationStatus.ACCEPTED || jobApplication.getStatus() == ApplicationStatus.REJECTED) {
            throw new ConflictException("Application cannot be rejected in its current status");
        }

        jobApplication.setStatus(ApplicationStatus.REJECTED);
        JobApplication savedJobApplication = jobApplicationRepository.save(jobApplication);
        return jobApplicationMapper.toCompanyResponse(savedJobApplication);
    }

    @Transactional
    @Override
    public JobApplicationCompanyResponse review(Long applicationId, User user){
        JobApplication jobApplication = jobApplicationRepository.findById(applicationId).orElseThrow(() -> new ResourceNotFoundException("Not found Job application with id: " + applicationId));
        if(user.getCompany() == null || !jobApplication.getJob().getCompany().getId().equals(user.getCompany().getId())) {
            throw new ForbiddenException("You cannot access applications for another company's job");
        }

        if (jobApplication.getStatus() != ApplicationStatus.PENDING) {
            throw new ConflictException("Application can only be reviewed when it is pending");
        }

        jobApplication.setStatus(ApplicationStatus.REVIEWED);
        JobApplication savedJobApplication = jobApplicationRepository.save(jobApplication);
        return jobApplicationMapper.toCompanyResponse(savedJobApplication);
    }

    @Transactional
    @Override
    public JobApplicationCompanyResponse interview(Long applicationId, User user){
        JobApplication jobApplication = jobApplicationRepository.findById(applicationId).orElseThrow(() -> new ResourceNotFoundException("Not found Job application with id: " + applicationId));
        if(user.getCompany() == null || !jobApplication.getJob().getCompany().getId().equals(user.getCompany().getId())) {
            throw new ForbiddenException("You cannot access applications for another company's job");
        }

        if (jobApplication.getStatus() != ApplicationStatus.REVIEWED) {
            throw new ConflictException("Application can only move to interview after being reviewed");
        }

        jobApplication.setStatus(ApplicationStatus.INTERVIEW);
        JobApplication savedJobApplication = jobApplicationRepository.save(jobApplication);
        return jobApplicationMapper.toCompanyResponse(savedJobApplication);
    }

    @Transactional
    @Override
    public void withdraw(Long id, User user) {
        JobApplication jobApplication = jobApplicationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found Job application with id: " + id));
        if(!jobApplication.getUser().getId().equals(user.getId())){
            throw new ForbiddenException("You cannot access other user's application");
        }

        jobApplicationRepository.delete(jobApplication);

    }
}
