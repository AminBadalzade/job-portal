package com.amin.jobportal.mapper;

import com.amin.jobportal.dto.response.ResumeResponse;
import com.amin.jobportal.entity.Resume;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResumeMapper {
    @Mapping(source = "originalFileName", target = "fileName")
    @Mapping(source = "storageKey", target = "downloadUrl")
    ResumeResponse toResponse(Resume resume);
}
