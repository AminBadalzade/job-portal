package com.amin.jobportal.service;

import com.amin.jobportal.dto.request.CompanyRequest;
import com.amin.jobportal.dto.request.UpdateCompanyRequest;
import com.amin.jobportal.dto.response.CompanyResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {
    @Override
    public CompanyResponse create(CompanyRequest request) {
        return null;
    }

    @Override
    public CompanyResponse getById(Long id) {
        return null;
    }

    @Override
    public CompanyResponse update(Long id, UpdateCompanyRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<CompanyResponse> getAll() {
        return List.of();
    }
}
