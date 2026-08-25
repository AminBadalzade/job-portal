package com.amin.jobportal.mapper;

import com.amin.jobportal.dto.request.RegisterRequest;
import com.amin.jobportal.dto.response.UserResponse;
import com.amin.jobportal.dto.response.UserSummaryResponse;
import com.amin.jobportal.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CompanyMapper.class)
public interface UserMapper {
    UserResponse toResponse(User user);

    UserSummaryResponse toSummaryResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(RegisterRequest request);

}
