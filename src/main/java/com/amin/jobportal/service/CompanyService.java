package com.amin.jobportal.service;

import com.amin.jobportal.dto.request.CompanyRequest;
import com.amin.jobportal.dto.request.UpdateCompanyRequest;
import com.amin.jobportal.dto.response.CompanyResponse;
import com.amin.jobportal.dto.response.CompanySummaryResponse;
import com.amin.jobportal.dto.response.JobSummaryResponse;
import com.amin.jobportal.entity.User;

import java.util.List;

public interface CompanyService {
    CompanyResponse create(CompanyRequest request, User user);

    CompanyResponse getById(Long id);

    CompanyResponse update(Long id, UpdateCompanyRequest request, User user);

    void delete(Long id, User user);

    List<CompanySummaryResponse> getAll(String name);

    List<JobSummaryResponse> getJobsByCompanyId(Long id);
}
