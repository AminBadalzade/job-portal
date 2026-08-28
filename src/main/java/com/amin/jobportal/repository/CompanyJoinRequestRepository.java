package com.amin.jobportal.repository;

import com.amin.jobportal.entity.CompanyJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyJoinRequestRepository extends JpaRepository<CompanyJoinRequest, Long> {
    void deleteCompanyJoinRequestsByCompanyId(Long companyId);

}
