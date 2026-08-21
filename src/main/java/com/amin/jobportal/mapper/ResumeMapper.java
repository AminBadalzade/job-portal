package com.amin.jobportal.mapper;

import com.amin.jobportal.dto.response.ResumeResponse;
import com.amin.jobportal.entity.Resume;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResumeMapper {
    ResumeResponse toResponse(Resume resume);
}
