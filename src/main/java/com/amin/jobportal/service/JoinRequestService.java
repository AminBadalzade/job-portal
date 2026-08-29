package com.amin.jobportal.service;

import com.amin.jobportal.dto.response.CompanyJoinRequestResponse;
import com.amin.jobportal.entity.CompanyJoinRequest;
import com.amin.jobportal.entity.User;

import java.util.List;

public interface JoinRequestService {
    List<CompanyJoinRequestResponse> getAllRequests(Long companyId, User user);
    CompanyJoinRequestResponse joinCompany(Long companyId, User user);

    CompanyJoinRequestResponse approveRequest(Long companyId, Long requestId, User currentUser);

    CompanyJoinRequestResponse rejectRequest(Long companyId, Long requestId, User currentUser);
}
