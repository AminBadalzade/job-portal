package com.amin.jobportal.mapper;

import com.amin.jobportal.dto.request.CreateJobRequest;
import com.amin.jobportal.dto.request.UpdateJobRequest;
import com.amin.jobportal.dto.response.JobResponse;
import com.amin.jobportal.dto.response.JobSummaryResponse;
import com.amin.jobportal.entity.Job;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = CompanyMapper.class)
public interface JobMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Job toEntity(CreateJobRequest createJobRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromRequest(UpdateJobRequest request,
                           @MappingTarget Job job);

    @Mapping(source = "company.name", target = "companyName")
    JobSummaryResponse toSummaryResponse(Job job);

    List<JobSummaryResponse> toSummaryResponseList(List<Job> jobs);

    JobResponse toResponse(Job job);


}
