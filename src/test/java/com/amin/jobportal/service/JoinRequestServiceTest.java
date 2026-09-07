package com.amin.jobportal.service;

import com.amin.jobportal.dto.response.CompanyJoinRequestResponse;
import com.amin.jobportal.entity.Company;
import com.amin.jobportal.entity.CompanyJoinRequest;
import com.amin.jobportal.entity.User;
import com.amin.jobportal.enums.Role;
import com.amin.jobportal.exception.ForbiddenException;
import com.amin.jobportal.exception.ResourceNotFoundException;
import com.amin.jobportal.mapper.CompanyJoinRequestMapper;
import com.amin.jobportal.repository.CompanyJoinRequestRepository;
import com.amin.jobportal.repository.CompanyRepository;
import com.amin.jobportal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JoinRequestServiceTest {

    @Mock
    private CompanyJoinRequestRepository companyJoinRequestRepository;

    @Mock
    private CompanyJoinRequestMapper companyJoinRequestMapper;

    @Mock
    private  CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JoinRequestServiceImpl joinRequestService;

    private User employee;
    private User ownerCompany;

    private Long companyId;

    private Company company;

    @BeforeEach
    void setUp(){
        employee = new User();
        employee.setId(10L);
        employee.setRole(Role.EMPLOYER);

        ownerCompany = new User();
        ownerCompany.setId(20L);
        ownerCompany.setRole(Role.EMPLOYER);

        companyId = 1L;

        company = new Company();
        company.setId(companyId);
        company.setOwner(ownerCompany);
    }

    @Test
    void getAllRequestsSuccesfully(){
        // Arrange
        CompanyJoinRequest companyJoinRequest = new CompanyJoinRequest();
        companyJoinRequest.setId(21L);

        CompanyJoinRequest companyJoinRequest2 = new CompanyJoinRequest();
        companyJoinRequest2.setId(22L);

        List<CompanyJoinRequest> companyJoinRequestList = List.of(companyJoinRequest, companyJoinRequest2);

        CompanyJoinRequestResponse companyJoinRequestResponse = new CompanyJoinRequestResponse();
        companyJoinRequestResponse.setId(21L);

        CompanyJoinRequestResponse companyJoinRequestResponse2 = new CompanyJoinRequestResponse();
        companyJoinRequestResponse2.setId(22L);

        List<CompanyJoinRequestResponse> companyJoinRequestResponseList = List.of(companyJoinRequestResponse, companyJoinRequestResponse2);


        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyJoinRequestRepository.findByCompanyId(companyId)).thenReturn(companyJoinRequestList);

        when(companyJoinRequestMapper.toResponse(companyJoinRequest)).thenReturn(companyJoinRequestResponse);
        when(companyJoinRequestMapper.toResponse(companyJoinRequest2)).thenReturn(companyJoinRequestResponse2);

        // Act
        List<CompanyJoinRequestResponse> actualResponses = joinRequestService.getAllRequests(companyId, ownerCompany);

        // Assert
        assertEquals(2, actualResponses.size());
        assertEquals(21L, actualResponses.get(0).getId());
        assertEquals(22L, actualResponses.get(1).getId());

        verify(companyRepository).findById(companyId);
        verify(companyJoinRequestRepository).findByCompanyId(companyId);
        verify(companyJoinRequestMapper, times(2)).toResponse(any(CompanyJoinRequest.class));
    }

    @Test
    void getAllRequests_WhenUserIsNotOwner_ShouldThrowForbiddenException(){
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> joinRequestService.getAllRequests(companyId, employee)
        );

        assertEquals("Only the company owner can perform this action", exception.getMessage());


        verify(companyJoinRequestRepository, never()).findByCompanyId(any());
        verify(companyJoinRequestMapper, never()).toResponse(any());
    }

    @Test
    void getAllRequests_Exception_WhenCompanyNotFound(){
        Long companyRequestId = 20L;

        when(companyRepository.findById(companyRequestId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows
                (ResourceNotFoundException.class,
                        () -> joinRequestService.getAllRequests(companyRequestId, employee));

        assertThat(exception.getMessage()).isEqualTo("Company not found with id: " + companyRequestId);

        verify(companyJoinRequestRepository, never()).delete(any(CompanyJoinRequest.class));

    }

    @Test
    void getAllRequests_ForbiddenException_WhenCompanyOwnerIsNull(){
        company.setOwner(null);
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> joinRequestService.getAllRequests(companyId, ownerCompany)
        );

        assertEquals("Only the company owner can perform this action", exception.getMessage());

        verify(companyJoinRequestRepository, never()).findByCompanyId(any());
    }



}
