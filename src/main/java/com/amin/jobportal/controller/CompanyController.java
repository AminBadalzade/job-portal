package com.amin.jobportal.controller;

import com.amin.jobportal.dto.request.CompanyRequest;
import com.amin.jobportal.dto.request.UpdateCompanyRequest;
import com.amin.jobportal.dto.response.CompanyResponse;
import com.amin.jobportal.dto.response.CompanySummaryResponse;
import com.amin.jobportal.dto.response.JobSummaryResponse;
import com.amin.jobportal.entity.User;
import com.amin.jobportal.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    public final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public ResponseEntity<List<CompanySummaryResponse>> getAll(@RequestParam(required = false) String name){
        List<CompanySummaryResponse> companySummaryResponses = companyService.getAll(name);

        return ResponseEntity.ok(companySummaryResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getById(@PathVariable Long id){
        return  ResponseEntity.ok(companyService.getById(id));
    }

    @GetMapping("/{id}/jobs")
    public ResponseEntity<List<JobSummaryResponse>> getJobsByCompanyId(@PathVariable Long id){
        return ResponseEntity.ok(companyService.getJobsByCompanyId(id));
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateCompanyRequest updateCompanyRequest, @AuthenticationPrincipal User user ){
        return new ResponseEntity<>(companyService.update(id, updateCompanyRequest, user), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @PostMapping
    public ResponseEntity<CompanyResponse> create(@Valid @RequestBody CompanyRequest companyRequest,@AuthenticationPrincipal User user){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyService.create(companyRequest, user));
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user){
        companyService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
