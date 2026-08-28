package com.amin.jobportal.repository;

import com.amin.jobportal.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    void deleteJobApplicationsByJobIdIn(List<Long> jobIds);
}
