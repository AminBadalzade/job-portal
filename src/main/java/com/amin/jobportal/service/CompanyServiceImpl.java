package com.amin.jobportal.service;

import com.amin.jobportal.dto.request.CompanyRequest;
import com.amin.jobportal.dto.request.UpdateCompanyRequest;
import com.amin.jobportal.dto.response.CompanyResponse;
import com.amin.jobportal.dto.response.CompanySummaryResponse;
import com.amin.jobportal.dto.response.JobSummaryResponse;
import com.amin.jobportal.entity.Company;
import com.amin.jobportal.entity.CompanyJoinRequest;
import com.amin.jobportal.entity.Job;
import com.amin.jobportal.entity.User;
import com.amin.jobportal.exception.ForbiddenException;
import com.amin.jobportal.exception.ResourceNotFoundException;
import com.amin.jobportal.mapper.CompanyMapper;
import com.amin.jobportal.mapper.JobMapper;
import com.amin.jobportal.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    public final CompanyRepository companyRepository;
    public final CompanyMapper companyMapper;
    public final UserRepository userRepository;
    public final JobRepository jobRepository;
    public final JobMapper jobMapper;
    public final CompanyJoinRequestRepository companyJoinRequestRepository;
    public final JobApplicationRepository jobApplicationRepository;


    public CompanyServiceImpl(CompanyRepository companyRepository, CompanyMapper companyMapper, UserRepository userRepository, JobRepository jobRepository, JobMapper jobMapper, CompanyJoinRequestRepository companyJoinRequestRepository, JobApplicationRepository jobApplicationRepository) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.jobMapper = jobMapper;
        this.companyJoinRequestRepository = companyJoinRequestRepository;
        this.jobApplicationRepository = jobApplicationRepository;
    }

    @Override
    public List<CompanySummaryResponse> getAll(String name) {
        List<Company> companyList;

        if (name == null || name.isBlank()) {
            companyList = companyRepository.findAll();
        } else {
            companyList = companyRepository.findByNameContainingIgnoreCase(name);
        }

        return companyList.stream()
                .map(companyMapper::toSummaryResponse)
                .toList();
    }

    @Override
    public CompanyResponse getById(Long id) {
        Company company = companyRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("Not found company with id: "+ id));

        return companyMapper.toResponse(company);
    }

    @Override
    public  List<JobSummaryResponse> getJobsByCompanyId(Long id){
       List<Job> jobList = jobRepository.getJobByCompanyId(id);

       return jobList.stream().map(jobMapper::toSummaryResponse).toList();
    }

    @Transactional
    @Override
    public CompanyResponse create(CompanyRequest request, User user) {
           if (user.getCompany() != null){
               throw new ForbiddenException("You are working in company: " + user.getCompany().getName());
           }

           if (companyRepository.findCompanyByName(request.getName()).isPresent()){
               throw new ForbiddenException("You cannot create existing company");
           }

           Company company = companyMapper.toEntity(request);
           company.setOwner(user);

           user.setCompany(company);

          Company companyDb = companyRepository.save(company);
          userRepository.save(user);

          return companyMapper.toResponse(companyDb);
    }


    @Transactional
    @Override
    public CompanyResponse update(Long id, UpdateCompanyRequest request, User user) {
        Company company = companyRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Not found company with id: " + id));

        if(user.getCompany() == null){
            throw new ForbiddenException("You need to have company");
        }

        if(!user.getCompany().getId().equals(company.getId())){
            throw new ForbiddenException("You are not authorized to update");
        }

        if(!company.getOwner().getId().equals(user.getId())){
            throw new ForbiddenException("Only owner can update company details");
        }

        company.setName(request.getName());
        company.setDescription(request.getDescription());
        company.setIndustry(request.getIndustry());

        Company companyDb = companyRepository.save(company);

        return companyMapper.toResponse(companyDb);
    }

    @Transactional
    @Override
    public void delete(Long id, User user) {
        Company company = companyRepository.findById(id).orElseThrow(()
                -> new ResourceNotFoundException("Not found company with id: " + id));

        if(user.getCompany() == null){
            throw new ForbiddenException("You need to have company firstly");
        }

        if(!user.getCompany().getId().equals(company.getId())){
            throw new ForbiddenException("You are not authorized to delete");
        }

        if(!company.getOwner().getId().equals(user.getId())){
            throw new ForbiddenException("Only owner can delete company");
        }

        List<Long> jobIds = jobRepository.getJobByCompanyId(id).stream()
                .map(Job::getId)
                .toList();

        if (!jobIds.isEmpty()) {
            jobApplicationRepository.deleteJobApplicationsByJobIdIn(jobIds);
        }
        jobRepository.deleteByCompanyId(id);

        companyJoinRequestRepository.deleteCompanyJoinRequestsByCompanyId(id);

        userRepository.findUsersByCompanyId(id).forEach(u -> u.setCompany(null));

        companyRepository.delete(company);
    }

}
