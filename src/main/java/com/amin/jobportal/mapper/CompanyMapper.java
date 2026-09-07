package com.amin.jobportal.mapper;

import com.amin.jobportal.dto.request.CompanyRequest;
import com.amin.jobportal.dto.request.UpdateCompanyRequest;
import com.amin.jobportal.dto.response.CompanyResponse;
import com.amin.jobportal.dto.response.CompanySummaryResponse;
import com.amin.jobportal.dto.response.MyCompanyResponse;
import com.amin.jobportal.entity.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Company toEntity(CompanyRequest companyRequest);

    CompanyResponse toResponse(Company company);

    CompanySummaryResponse toSummaryResponse(Company company);

    @Mapping(source = "owner.id", target = "ownerId")
    MyCompanyResponse toMyCompanyResponse(Company company);
   }
