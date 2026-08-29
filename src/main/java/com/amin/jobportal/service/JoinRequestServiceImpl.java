package com.amin.jobportal.service;

import com.amin.jobportal.dto.response.CompanyJoinRequestResponse;
import com.amin.jobportal.entity.Company;
import com.amin.jobportal.entity.CompanyJoinRequest;
import com.amin.jobportal.entity.User;
import com.amin.jobportal.enums.JoinRequestStatus;
import com.amin.jobportal.exception.ConflictException;
import com.amin.jobportal.exception.ForbiddenException;
import com.amin.jobportal.exception.ResourceNotFoundException;
import com.amin.jobportal.mapper.CompanyJoinRequestMapper;
import com.amin.jobportal.repository.CompanyJoinRequestRepository;
import com.amin.jobportal.repository.CompanyRepository;
import com.amin.jobportal.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JoinRequestServiceImpl implements  JoinRequestService {

    private final CompanyJoinRequestRepository companyJoinRequestRepository;
    private final CompanyJoinRequestMapper companyJoinRequestMapper;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;


    public JoinRequestServiceImpl(CompanyJoinRequestRepository companyJoinRequestRepository, CompanyJoinRequestMapper companyJoinRequestMapper, CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyJoinRequestRepository = companyJoinRequestRepository;
        this.companyJoinRequestMapper = companyJoinRequestMapper;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }


    @Override
    public List<CompanyJoinRequestResponse> getAllRequests(Long companyId, User user){
        Company company = companyRepository.findById(companyId).orElseThrow(() ->
                new ResourceNotFoundException("Company not found with id: " + companyId));

        assertIsOwner(company, user);

       List<CompanyJoinRequest> companyJoinRequestList = companyJoinRequestRepository.findByCompanyId(companyId);

       return companyJoinRequestList.stream().map(companyJoinRequestMapper::toResponse).toList();
    }

    @Transactional
    @Override
    public CompanyJoinRequestResponse joinCompany(Long companyId, User user){
        Company company = companyRepository.findById(companyId).orElseThrow(() ->
                new ResourceNotFoundException("Company not found with id: " + companyId));

        if (user.getCompany() != null) {
            throw new ConflictException("You are already employed at company: " + user.getCompany().getName());
        }

        boolean hasPendingRequest = companyJoinRequestRepository
                .existsByUserIdAndCompanyIdAndStatus(user.getId(), companyId, JoinRequestStatus.PENDING);

        if (hasPendingRequest) {
            throw new ConflictException("You already have a pending join request for this company");
        }

        CompanyJoinRequest companyJoinRequest = new CompanyJoinRequest();

        companyJoinRequest.setUser(user);
        companyJoinRequest.setCompany(company);
        companyJoinRequest.setStatus(JoinRequestStatus.PENDING); // Explicitly set default status

        CompanyJoinRequest savedRequest = companyJoinRequestRepository.save(companyJoinRequest);
        return companyJoinRequestMapper.toResponse(savedRequest);

    }

    @Override
    @Transactional
    public CompanyJoinRequestResponse approveRequest(Long companyId, Long requestId, User currentUser){
        // checking whether company exists or not
        Company company = companyRepository.findById(companyId).orElseThrow(() ->
                new ResourceNotFoundException("Company not found with id: " + companyId));

        // checking the owner of company
        assertIsOwner(company, currentUser);

        // do we have such requestId with companyId? uri correct?
        CompanyJoinRequest joinRequest = companyJoinRequestRepository.findByIdAndCompanyId(requestId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Join request not found with id: " + requestId));

        // already processed?
        if (joinRequest.getStatus() != JoinRequestStatus.PENDING) {
            throw new ConflictException("Request has already been processed");
        }

        // getting user from request entity
        User applicant = joinRequest.getUser();
        if (applicant.getCompany() != null) {
            throw new ConflictException("User is already employed at another company");
        }

        // changing status and adding applicant to company
        joinRequest.setStatus(JoinRequestStatus.APPROVED);
        applicant.setCompany(company);

        // updating user details and request details
        userRepository.save(applicant);
        CompanyJoinRequest updatedRequest = companyJoinRequestRepository.save(joinRequest);
        return companyJoinRequestMapper.toResponse(updatedRequest);
    }

    @Transactional
    @Override
    public CompanyJoinRequestResponse rejectRequest(Long companyId, Long requestId, User currentUser){
        // checking whether company exists or not
        Company company = companyRepository.findById(companyId).orElseThrow(() ->
                new ResourceNotFoundException("Company not found with id: " + companyId));

        // checking the owner of company
        assertIsOwner(company, currentUser);

        // do we have such requestId with companyId? uri correct?
        CompanyJoinRequest joinRequest = companyJoinRequestRepository.findByIdAndCompanyId(requestId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Join request not found with id: " + requestId));

        // already processed?
        if (joinRequest.getStatus() != JoinRequestStatus.PENDING) {
            throw new ConflictException("Request has already been processed");
        }

        joinRequest.setStatus(JoinRequestStatus.REJECTED);
        CompanyJoinRequest updatedRequest = companyJoinRequestRepository.save(joinRequest);

        return companyJoinRequestMapper.toResponse(updatedRequest);
    }

    private void assertIsOwner(Company company, User user) {
        if (company.getOwner() == null || !company.getOwner().getId().equals(user.getId())) {
            throw new ForbiddenException("Only the company owner can perform this action");
        }
    }


}
