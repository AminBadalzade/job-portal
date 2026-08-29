package com.amin.jobportal.mapper;

import com.amin.jobportal.dto.response.CompanyJoinRequestResponse;
import com.amin.jobportal.entity.CompanyJoinRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyJoinRequestMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")

    CompanyJoinRequestResponse toResponse(CompanyJoinRequest entity);
}
