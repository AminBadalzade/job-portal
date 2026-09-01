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
import org.glassfish.jaxb.runtime.v2.runtime.output.SAXOutput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

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
    private CompanyRepository companyRepository;

    @Mock
    JobMapper jobMapper;

    @Mock
    JobRepository jobRepository;

    @Test
    void updateJobSuccesfullyByEmployee(){
        Long jobId = 1L;

        Company company = new Company();
        company.setId(10L);

        User user = new User();
        user.setCompany(company);

        Job job = new Job();
        job.setId(jobId);
        job.setTitle("Backend Engineer");
        job.setCompany(company);

        UpdateJobRequest request = new UpdateJobRequest();
        request.setTitle("Senior Backend Engineer");

        JobResponse jobResponse = new JobResponse();
        jobResponse.setId(jobId);
        jobResponse.setTitle("Senior Backend Engineer");

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(jobMapper.toResponse(job)).thenReturn(jobResponse);

        //Act
        JobResponse result = jobService.update(jobId, request, user);

        // Assert
        assertThat(result.getId()).isEqualTo(jobId);
        assertThat(result.getTitle()).isEqualTo("Senior Backend Engineer");

        //verification
        verify(jobRepository, times(1)).findById(jobId);
        verify(jobMapper, times(1)).updateFromRequest(request, job);
        verify(jobMapper, times(1)).toResponse(job);

    }

    @Test
    void notFoundJobToUpdate(){
        Long jobId = 999L;

        User user = new User();

        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                jobService.update(jobId, new UpdateJobRequest(), user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Job not found with id: " + jobId);
        // Verify
        verify(jobMapper, never()).updateFromRequest(any(), any());
        verify(jobMapper, never()).toResponse(any());
    }

    @Test
    void userNotHaveAuthorizationtoUpdate(){
        Long jobId = 1L;

        Company company = new Company();
        company.setId(10L);

        Company userCompany = new Company();
        userCompany.setId(20L);

        User user = new User();
        user.setCompany(userCompany);

        Job job = new Job();
        job.setId(jobId);
        job.setTitle("Backend Engineer");
        job.setCompany(company);

        UpdateJobRequest request = new UpdateJobRequest();
        request.setTitle("Senior Backend Engineer");

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        assertThatThrownBy(()->
                jobService.update(jobId, request, user))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("You are not authorized to access other job's details");

        verify(jobRepository, times(1)).findById(jobId);
        verify(jobMapper, never()).updateFromRequest(any(), any());
        verify(jobMapper, never()).toResponse(job);

    }

    @Test
    void deleteJobSuccesfullyAsAdmin(){
        Long jobId = 1L;

        Company company = new Company();
        company.setId(10L);

        User user = new User();
        user.setRole(Role.ADMIN);

        Job job = new Job();
        job.setId(jobId);
        job.setTitle("Backend Engineer");
        job.setCompany(company);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        //Act
        jobService.delete(jobId, user);

        //Verify
        verify(jobRepository, times(1)).findById(jobId);
        verify(jobRepository, times(1)).delete(job);
    }

    @Test
    void deleteJobSuccesfullyByEmployee(){
        Long jobId = 1L;

        Company company = new Company();
        company.setId(10L);

        User user = new User();
        user.setRole(Role.EMPLOYER);
        user.setCompany(company);

        Job job = new Job();
        job.setId(jobId);
        job.setTitle("Backend Engineer");
        job.setCompany(company);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        jobService.delete(jobId, user);

        verify(jobRepository, times(1)).findById(jobId);
        verify(jobRepository, times(1)).delete(job);
    }

    @Test
    void employerCannotDeleteOtherCompanyJob(){
        Long jobId = 1L;

        Company company = new Company();
        company.setId(10L);

        Company userCompany = new Company();
        userCompany.setId(20L);

        User user = new User();
        user.setRole(Role.EMPLOYER);
        user.setCompany(userCompany);

        Job job = new Job();
        job.setId(jobId);
        job.setTitle("Backend Engineer");
        job.setCompany(company);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        assertThatThrownBy(()->
                jobService.delete(jobId,user)).isInstanceOf(ForbiddenException.class)
                        .hasMessage("You are not authorized to delete job");

        verify(jobRepository, times(1)).findById(jobId);
        verify(jobRepository,never()).delete(job);
    }

    @Test
    void notFoundJobToDelete() {
        Long jobId = 999L;

        User user = new User();
        user.setRole(Role.EMPLOYER);

        when(jobRepository.findById(jobId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                jobService.delete(jobId, user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Job not found with id: " + jobId);

        verify(jobRepository, times(1))
                .findById(jobId);

        verify(jobRepository, never())
                .delete(any(Job.class));
    }

    @Test
    void searchJobsSuccessfully() {
        // Arrange
        JobSearchRequest request = new JobSearchRequest();
        request.setTitle("Backend");
        request.setCity("Vilnius");

        Pageable pageable = PageRequest.of(0, 10);

        Job job = new Job();
        job.setId(1L);
        job.setTitle("Backend Engineer");

        Job job2 = new Job();
        job2.setId(2L);
        job2.setTitle("Senior Backend Engineer");

        JobSummaryResponse response1 = new JobSummaryResponse();
        response1.setId(1L);
        response1.setTitle("Backend Engineer");

        JobSummaryResponse response2 = new JobSummaryResponse();
        response2.setId(2L);
        response2.setTitle("Senior Backend Engineer");

        Page<Job> jobsFromDb =
                new PageImpl<>(List.of(job, job2), pageable, 2);

        when(jobRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(jobsFromDb);

        when(jobMapper.toSummaryResponse(job))
                .thenReturn(response1);

        when(jobMapper.toSummaryResponse(job2))
                .thenReturn(response2);

        // Act
        Page<JobSummaryResponse> result =
                jobService.search(request, pageable);

        // Assert
        assertThat(result.getContent().size()).isEqualTo(2);

        assertThat(result.getContent().get(0).getId())
                .isEqualTo(1L);

        assertThat(result.getContent().get(0).getTitle())
                .isEqualTo("Backend Engineer");

        assertThat(result.getContent().get(1).getId())
                .isEqualTo(2L);

        assertThat(result.getContent().get(1).getTitle())
                .isEqualTo("Senior Backend Engineer");

        // Verify
        verify(jobRepository, times(1))
                .findAll(any(Specification.class), eq(pageable));

        verify(jobMapper, times(1))
                .toSummaryResponse(job);

        verify(jobMapper, times(1))
                .toSummaryResponse(job2);
    }

    @Test
    void createJobSuccesfully(){
        //Assert
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


        when(companyRepository.findById(10L))
                .thenReturn(Optional.of(company));
        when(jobMapper.toEntity(createJobRequest)).thenReturn(jobToSave);
        when(jobRepository.save(jobToSave)).thenReturn(savedJob);
        when(jobMapper.toResponse(savedJob)).thenReturn(jobResponse);

        // Act
        JobResponse result = jobService.create(createJobRequest, user);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(jobToSave.getCompany()).isEqualTo(company);

        verify(jobMapper).toEntity(createJobRequest);
        verify(jobRepository).save(jobToSave);
        verify(jobMapper).toResponse(savedJob);
        verify(companyRepository).findById(10L);
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
            verify(jobMapper, never()).toResponse(any());
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

    @Test
    void getCompanysJobsWhenNoJobsExist() {
        Long companyId = 1L;

        when(jobRepository.getJobByCompanyId(companyId))
                .thenReturn(List.of());

        List<JobSummaryResponse> result =
                jobService.getCompanyJobs(companyId);

        assertThat(result.isEmpty()).isTrue();

        verify(jobRepository).getJobByCompanyId(companyId);
        verify(jobMapper, never()).toSummaryResponseList(any());
    }

}
