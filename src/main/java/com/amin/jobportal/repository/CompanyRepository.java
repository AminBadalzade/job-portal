package com.amin.jobportal.repository;

import com.amin.jobportal.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findCompanyByName(String name);
    List<Company> findByNameContainingIgnoreCase(String name);
}
