package com.amin.jobportal.service;

import com.amin.jobportal.dto.request.CreateJobRequest;
import com.amin.jobportal.dto.response.JobResponse;
import com.amin.jobportal.dto.response.JobSummaryResponse;
import com.amin.jobportal.entity.Company;
import com.amin.jobportal.entity.Job;
import com.amin.jobportal.entity.User;
import com.amin.jobportal.exception.ResourceNotFoundException;
import com.amin.jobportal.mapper.JobMapper;
import com.amin.jobportal.repository.JobRepository;
import org.glassfish.jaxb.runtime.v2.runtime.output.SAXOutput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {

    @InjectMocks
    JobServiceImpl jobService;

    @Mock
    JobMapper jobMapper;

    @Mock
    JobRepository jobRepository;


    @Test
    void myFirstTest(){
        System.out.println("my first unit test");
    }

    @Test
    void createJobSuccesfully(){
        //Assert
        System.out.println("First unit test");
        Company company = new Company();
        company.setId(10L);

        User user = new User();
        user.setCompany(company);

        // job request create
        CreateJobRequest createJobRequest = new CreateJobRequest();
        createJobRequest.setTitle("Backend engineer");

        // just job to save after mapping request
        Job jobToSave = new Job();
        jobToSave.setTitle("Backend Engineer");

        // saved job to return when we mock
        // adding to db
        Job savedJob = new Job();
        savedJob.setId(1L);
        savedJob.setTitle("Backend Engineer");
        savedJob.setCompany(company);

        // return as response
        JobResponse jobResponse = new JobResponse();
        jobResponse.setId(1L);
        jobResponse.setTitle("Backend Engineer");

        when(jobMapper.toEntity(createJobRequest)).thenReturn(jobToSave);
        when(jobRepository.save(jobToSave)).thenReturn(savedJob);
        when(jobMapper.toResponse(savedJob)).thenReturn(jobResponse);

        // Act
        JobResponse result = jobService.create(createJobRequest, user);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(jobToSave.getCompany()).isEqualTo(company);

        verify(jobRepository).save(jobToSave);

    }


    // Test getting job using id
    @Test
    void getJobByIdSuccessfully(){
        Long jobId = 1L;

        Job job = new Job();
        job.setId(jobId);
        job.setTitle("Backend Engineer");

        // return as response
        JobResponse jobResponse = new JobResponse();
        jobResponse.setId(jobId);
        jobResponse.setTitle("Backend Engineer");

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(jobMapper.toResponse(job)).thenReturn(jobResponse);

        //Act
        JobResponse result = jobService.getById(jobId);


        assertThat(result.getId()).isEqualTo(jobId);
        assertThat(result.getTitle()).isEqualTo(job.getTitle());

        verify(jobMapper, times(1)).toResponse(job);
        verify(jobRepository, times(1)).findById(jobId);
    }

    // testing exception when no found Job
    @Test
    void notFoundJobExceptionWithId(){
            Long jobId = 999L;

            when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> jobService.getById(jobId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Job not found with id: " + jobId);

            verify(jobRepository, times(1)).findById(jobId);
    }

    // Test getCompanyJob by companyId

    // Succesful version
    @Test
    void getCompanysJobsSuccesfully(){
        Long companyId = 1L;

        Job job = new Job();
        job.setId(1L);
        job.setTitle("Backend engineer");

        Job job2 = new Job();
        job2.setId(2L);
        job2.setTitle("Devops engineer");

        JobSummaryResponse response1 = new JobSummaryResponse();
        response1.setId(1L);
        response1.setTitle("Backend Engineer");

        JobSummaryResponse response2 = new JobSummaryResponse();
        response2.setId(2L);
        response2.setTitle("DevOps Engineer");

        List<Job> returnCompanyJobsFromDb = List.of(job, job2);
        List<JobSummaryResponse> jobSummaryResponseList = List.of(response1,response2);

        when(jobRepository.getJobByCompanyId(companyId)).thenReturn(returnCompanyJobsFromDb);
        when(jobMapper.toSummaryResponseList(returnCompanyJobsFromDb)).thenReturn(jobSummaryResponseList);

        List<JobSummaryResponse> result = jobService.getCompanyJobs(companyId);

        // asserting
        assertThat(result.size()).isEqualTo(2);

        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getTitle()).isEqualTo("Backend Engineer");

        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getTitle()).isEqualTo("DevOps Engineer");

        verify(jobRepository, times(1))
                .getJobByCompanyId(companyId);

        verify(jobMapper, times(1))
                .toSummaryResponseList(returnCompanyJobsFromDb);  }

}
