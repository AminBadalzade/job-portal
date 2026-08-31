package com.amin.jobportal.service;

import com.amin.jobportal.dto.request.CompanyRequest;
import com.amin.jobportal.dto.request.UpdateCompanyRequest;
import com.amin.jobportal.dto.response.CompanyResponse;
import com.amin.jobportal.dto.response.CompanySummaryResponse;
import com.amin.jobportal.dto.response.JobSummaryResponse;
import com.amin.jobportal.entity.Company;
import com.amin.jobportal.entity.Job;
import com.amin.jobportal.entity.User;
import com.amin.jobportal.exception.ConflictException;
import com.amin.jobportal.exception.ForbiddenException;
import com.amin.jobportal.exception.ResourceNotFoundException;
import com.amin.jobportal.mapper.CompanyMapper;
import com.amin.jobportal.mapper.JobMapper;
import com.amin.jobportal.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {
    @InjectMocks
    CompanyServiceImpl companyService;

    @Mock
    CompanyRepository companyRepository;

    @Mock
    CompanyMapper companyMapper;

    @Mock
    UserRepository userRepository;

    @Mock
    JobRepository jobRepository;

    @Mock
    JobMapper jobMapper;

    @Mock
    CompanyJoinRequestRepository companyJoinRequestRepository;

    @Mock
    JobApplicationRepository jobApplicationRepository;

    // Get all companies succesfully
    @Test
    void getAllCompaniesSuccessfully() {
        Company company1 = new Company();
        company1.setId(1L);
        company1.setName("Google");

        Company company2 = new Company();
        company2.setId(2L);
        company2.setName("Microsoft");

        CompanySummaryResponse response1 = new CompanySummaryResponse();
        response1.setId(1L); response1.setName("Google");

        CompanySummaryResponse response2 = new CompanySummaryResponse();
        response2.setId(2L); response2.setName("Microsoft");

        when(companyRepository.findAll()) .thenReturn(List.of(company1, company2));

        when(companyMapper.toSummaryResponse(company1)) .thenReturn(response1);
        when(companyMapper.toSummaryResponse(company2)) .thenReturn(response2);
        List<CompanySummaryResponse> result = companyService.getAll(null);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getName()).isEqualTo("Google");
        assertThat(result.get(1).getName()).isEqualTo("Microsoft");

        verify(companyRepository, times(1)).findAll();
        verify(companyMapper, times(1)).toSummaryResponse(company1);
        verify(companyMapper, times(1)).toSummaryResponse(company2);

    }

    @Test
    void getCompaniesByNameSuccessfully() {
        String name = "google";

        Company company1 = new Company();
        company1.setId(1L); company1.setName("Google");

        Company company2 = new Company(); company2.setId(2L);
        company2.setName("Google Cloud");

        CompanySummaryResponse response1 = new CompanySummaryResponse();
        response1.setId(1L);
        response1.setName("Google");

        CompanySummaryResponse response2 = new CompanySummaryResponse();
        response2.setId(2L);
        response2.setName("Google Cloud");

        when(companyRepository.findByNameContainingIgnoreCase(name)).thenReturn(List.of(company1, company2));
        when(companyMapper.toSummaryResponse(company1)).thenReturn(response1);
        when(companyMapper.toSummaryResponse(company2)).thenReturn(response2);

        // Implementing
        List<CompanySummaryResponse> result = companyService.getAll(name);

        //Asserting
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getName()).isEqualTo("Google");
        assertThat(result.get(1).getName()).isEqualTo("Google Cloud");

        //Verify
        verify(companyRepository, times(1)).findByNameContainingIgnoreCase(name);
        verify(companyRepository, never()).findAll();
    }

    // GET BY ID
    @Test
    void getCompanyByIdSuccessfully() {
        Long companyId = 1L;

        Company company = new Company();
        company.setId(companyId);
        company.setName("Google");

        CompanyResponse response = new CompanyResponse();
        response.setId(companyId);
        response.setName("Google");

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMapper.toResponse(company)).thenReturn(response);

        CompanyResponse result = companyService.getById(companyId);

        assertThat(result.getId()).isEqualTo(companyId);
        assertThat(result.getName()).isEqualTo("Google");

        verify(companyRepository, times(1)).findById(companyId);
        verify(companyMapper, times(1)).toResponse(company);
    }

    // Not found company ID
    @Test
    void notFoundCompanyById() {
        Long companyId = 999L;
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.getById(companyId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Not found company with id: " + companyId);

        verify(companyRepository, times(1)).findById(companyId);
        verify(companyMapper, never()).toResponse(any());
    }

    @Test
    void getCompanyJobsSuccessfully() {
        Long companyId = 1L;
        Job job1 = new Job();
        job1.setId(1L);
        job1.setTitle("Backend Engineer");

        Job job2 = new Job();
        job2.setId(2L);
        job2.setTitle("DevOps Engineer");

        JobSummaryResponse response1 = new JobSummaryResponse();
        response1.setId(1L);
        response1.setTitle("Backend Engineer");

        JobSummaryResponse response2 = new JobSummaryResponse();
        response2.setId(2L);
        response2.setTitle("DevOps Engineer");

        when(jobRepository.getJobByCompanyId(companyId)) .thenReturn(List.of(job1, job2));
        when(jobMapper.toSummaryResponse(job1)).thenReturn(response1);
        when(jobMapper.toSummaryResponse(job2)).thenReturn(response2);

        List<JobSummaryResponse> result = companyService.getJobsByCompanyId(companyId);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);

        verify(jobRepository, times(1)).getJobByCompanyId(companyId);
        verify(jobMapper, times(1)).toSummaryResponse(job1);
        verify(jobMapper, times(1)).toSummaryResponse(job2);
    }

    @Test
    void CreateCompanySuccesfully(){
        CompanyRequest companyRequest = new CompanyRequest();
        companyRequest.setName("Google");

        User user = new User();
        user.setId(1L);

        Company company = new Company();
        company.setId(10L);
        company.setName("Google");

        Company savedCompany = new Company();
        savedCompany.setId(10L);
        savedCompany.setName("Google");
        savedCompany.setOwner(user);

        CompanyResponse response = new CompanyResponse();
        response.setId(10L);
        response.setName("Google");

        when(companyRepository.findCompanyByName("Google"))
                .thenReturn(Optional.empty());

        when(companyMapper.toEntity(companyRequest))
                .thenReturn(company);

        when(companyRepository.save(company))
                .thenReturn(savedCompany);

        when(companyMapper.toResponse(savedCompany))
                .thenReturn(response);

        // Implement
        CompanyResponse result = companyService.create(companyRequest, user);

        // Assert
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Google");

        assertThat(company.getOwner()).isEqualTo(user);
        assertThat(user.getCompany()).isEqualTo(company);

        // Verify
        verify(companyRepository, times(1))
                .findCompanyByName("Google");
        verify(companyRepository, times(1))
                .save(company);
        verify(userRepository, times(1))
                .save(user);
        verify(companyMapper, times(1))
                .toEntity(companyRequest);
        verify(companyMapper, times(1))
                .toResponse(savedCompany);
    }

    // When user working in different company
    @Test
    void cannotCreateUserHasCompany(){
        Company userCompany = new Company();
        userCompany.setName("Microsoft");

        CompanyRequest request = new CompanyRequest();
        request.setName("Microsoft");

        User user = new User();
        user.setCompany(userCompany);

        assertThatThrownBy(() -> companyService.create(request, user))
                .isInstanceOf(ConflictException.class)
                .hasMessage("You are working in company: " + userCompany.getName());

        verify(companyRepository, never())
                .findCompanyByName(anyString());
        verify(companyRepository, never())
                .save(any());
        verify(userRepository, never())
                .save(any());

    }

    @Test
    void cannotCreateExistingCompany() {
        CompanyRequest request = new CompanyRequest();
        request.setName("Google");

        User user = new User();

        Company existingCompany = new Company();
        existingCompany.setName("Google");

        when(companyRepository.findCompanyByName("Google")).thenReturn(Optional.of(existingCompany));

        assertThatThrownBy(() -> companyService.create(request, user))
                .isInstanceOf(ConflictException.class)
                .hasMessage("You cannot create existing company");

        verify(companyRepository, times(1)) .findCompanyByName("Google");
        verify(companyRepository, never()) .save(any());
        verify(userRepository, never()) .save(any());
    }

    @Test
    void updateCompanyDetailsSuccesfully(){
        Long companyId = 10L;

        User user = new User();
        user.setId(1L);

        Company company = new Company();
        company.setId(10L);
        company.setName("Google");
        company.setOwner(user);

        user.setCompany(company);

        UpdateCompanyRequest request = new UpdateCompanyRequest();
        request.setName("Google for kids");
        request.setDescription("This company serves for young generation");
        request.setIndustry("Technology");

        CompanyResponse response = new CompanyResponse();
        response.setId(companyId);
        response.setName("Google for kids");

        when(companyRepository.findById(companyId))
                .thenReturn(Optional.of(company));

        when(companyRepository.save(company))
                .thenReturn(company);

        when(companyMapper.toResponse(company))
                .thenReturn(response);


        // Implementation
        CompanyResponse result = companyService.update(companyId, request, user);

        // Assert
        assertThat(result.getId()).isEqualTo(companyId);
        assertThat(result.getName()).isEqualTo("Google for kids");

        assertThat(company.getName()).isEqualTo("Google for kids");
        assertThat(company.getDescription()).isEqualTo("This company serves for young generation");
        assertThat(company.getIndustry()).isEqualTo("Technology");

        // Verify
        verify(companyRepository, times(1))
                .findById(companyId);
        verify(companyRepository, times(1))
                .save(company);

        verify(companyMapper, times(1))
                .toResponse(company);
    }

    @Test
    void cannotUpdateCompanyWhenUserHasNoCompany() {
        Long companyId = 10L;

        User user = new User();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(new Company()));

        assertThatThrownBy(() -> companyService.update( companyId, new UpdateCompanyRequest(), user))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("You need to have company");

        verify(companyRepository, never()) .save(any());
    }

    @Test
    void cannotUpdateOtherCompany() {
        Long companyId = 10L;

        Company userCompany = new Company();
        userCompany.setId(20L);

        Company targetCompany = new Company();
        targetCompany.setId(companyId);

        User user = new User();
        user.setCompany(userCompany);

        when(companyRepository.findById(companyId))
                .thenReturn(Optional.of(targetCompany));

        assertThatThrownBy(() -> companyService.update( companyId, new UpdateCompanyRequest(), user))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("You are not authorized to update");

        verify(companyRepository, never()) .save(any());
    }

    @Test
    void cannotUpdateCompanyWhenNotOwner() {
        Long companyId = 10L;

        User owner = new User();
        owner.setId(1L);

        User member = new User();
        member.setId(2L);

        Company company = new Company();
        company.setId(companyId);
        company.setOwner(owner);

        member.setCompany(company);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        assertThatThrownBy(() -> companyService.update( companyId, new UpdateCompanyRequest(), member))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Only owner can update company details"); verify(companyRepository, never()) .save(any());
    }

    @Test
    void notFoundCompanyToUpdate() {
        Long companyId = 999L;

        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.update( companyId, new UpdateCompanyRequest(), new User()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Not found company with id: " + companyId);

        verify(companyRepository, never()) .save(any());
    }

    @Test
    void deleteCompanySuccesfully(){
        Long companyId = 10L;

        User user = new User();
        user.setId(1L);

        Company company = new Company();
        company.setId(10L);
        company.setName("Google");
        company.setOwner(user);

        user.setCompany(company);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(jobRepository.getJobByCompanyId(companyId)) .thenReturn(List.of());
        when(userRepository.findUsersByCompanyId(companyId)) .thenReturn(List.of(user));

        companyService.delete(companyId, user);

        verify(companyRepository, times(1)).findById(companyId);
        verify(jobRepository, times(1)).getJobByCompanyId(companyId);
        verify(jobRepository, times(1)).deleteByCompanyId(companyId);
        verify(companyJoinRequestRepository, times(1)).deleteCompanyJoinRequestsByCompanyId(companyId);
        verify(userRepository, times(1)) .findUsersByCompanyId(companyId);
        verify(companyRepository, times(1)) .delete(company);

        assertThat(user.getCompany()).isNull();
    }

    @Test void cannotDeleteCompanyWhenUserHasNoCompany() {
        Long companyId = 10L;
        User user = new User();

        Company company = new Company();
        company.setId(companyId);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        assertThatThrownBy(() -> companyService.delete(companyId, user))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("You need to have company firstly");

        verify(companyRepository, never()) .delete(any());
    }

    @Test void cannotDeleteOtherCompany() {
        Long companyId = 10L;

        Company userCompany = new Company();
        userCompany.setId(20L);

        Company targetCompany = new Company();

        targetCompany.setId(companyId);

        User user = new User();
        user.setCompany(userCompany);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(targetCompany));

        assertThatThrownBy(() -> companyService.delete(companyId, user))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("You are not authorized to delete"); verify(companyRepository, never()) .delete(any());
    }

    @Test void cannotDeleteCompanyWhenNotOwner() {
        Long companyId = 10L;

        User owner = new User();
        owner.setId(1L);

        User member = new User();
        member.setId(2L);

        Company company = new Company();
        company.setId(companyId);
        company.setOwner(owner);

        member.setCompany(company);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        assertThatThrownBy(() -> companyService.delete(companyId, member))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Only owner can delete company");

        verify(companyRepository, never()) .delete(any());
    }

    @Test
    void notFoundCompanyToDelete() {
        Long companyId = 999L;

        User user = new User();

        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.delete(companyId, user)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Not found company with id: " + companyId);

        verify(companyRepository, never()) .delete(any());

    }

    @Test void deleteCompanyWithJobsSuccessfully() {
        Long companyId = 10L;

        User owner = new User();
        owner.setId(1L);

        Company company = new Company();
        company.setId(companyId);
        company.setOwner(owner);

        owner.setCompany(company);

        Job job1 = new Job();
        job1.setId(100L);
        job1.setCompany(company);

        Job job2 = new Job();
        job2.setId(200L);
        job2.setCompany(company);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        when(jobRepository.getJobByCompanyId(companyId)).thenReturn(List.of(job1, job2));

        when(userRepository.findUsersByCompanyId(companyId)).thenReturn(List.of(owner));

        companyService.delete(companyId, owner);

        verify(jobApplicationRepository, times(1))
                .deleteJobApplicationsByJobIdIn( List.of(100L, 200L));

        verify(jobRepository, times(1))
                .deleteByCompanyId(companyId);

        verify(companyJoinRequestRepository, times(1))
                .deleteCompanyJoinRequestsByCompanyId(companyId);

        verify(companyRepository, times(1))
                .delete(company);

    }
}
