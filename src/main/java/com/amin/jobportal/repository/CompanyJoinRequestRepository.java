package com.amin.jobportal.repository;

import com.amin.jobportal.entity.CompanyJoinRequest;
import com.amin.jobportal.enums.JoinRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyJoinRequestRepository extends JpaRepository<CompanyJoinRequest, Long> {
    void deleteCompanyJoinRequestsByCompanyId(Long companyId);

    @Query("select r from CompanyJoinRequest r JOIN  fetch r.user WHERE r.company.id = :companyId")
    List<CompanyJoinRequest> findByCompanyId(@Param("companyId") Long companyId);

    boolean existsByUserIdAndCompanyIdAndStatus(Long userId, Long companyId, JoinRequestStatus status);

    Optional<CompanyJoinRequest> findByIdAndCompanyId(Long id, Long CompanyId);


}
