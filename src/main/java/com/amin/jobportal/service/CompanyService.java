package com.amin.jobportal.service;

import com.amin.jobportal.dto.request.CompanyRequest;
import com.amin.jobportal.dto.request.UpdateCompanyRequest;
import com.amin.jobportal.dto.response.CompanyResponse;

import java.util.List;

public interface CompanyService {
    CompanyResponse create(CompanyRequest request);

    CompanyResponse getById(Long id);

    CompanyResponse update(Long id, UpdateCompanyRequest request);

    void delete(Long id);

    List<CompanyResponse> getAll();
}
