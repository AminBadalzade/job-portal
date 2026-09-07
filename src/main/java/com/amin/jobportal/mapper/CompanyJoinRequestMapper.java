package com.amin.jobportal.mapper;

import com.amin.jobportal.dto.response.CompanyJoinRequestResponse;
import com.amin.jobportal.dto.response.EmployerJoinRequestResponse;
import com.amin.jobportal.entity.CompanyJoinRequest;
import com.amin.jobportal.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface CompanyJoinRequestMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    CompanyJoinRequestResponse toResponse(CompanyJoinRequest entity);

    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    EmployerJoinRequestResponse toMyResponse(CompanyJoinRequest entity);
}