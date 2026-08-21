package com.amin.jobportal.mapper;

import com.amin.jobportal.dto.response.JobApplicationCompanyResponse;
import com.amin.jobportal.dto.response.JobApplicationSeekerResponse;
import com.amin.jobportal.entity.JobApplication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses={
        UserMapper.class, JobMapper.class, ResumeMapper.class
})
public interface JobApplicationMapper {
    @Mapping(source = "user", target = "applicant")
    JobApplicationCompanyResponse toCompanyResponse(JobApplication jobApplication);

    @Mapping(source = "job", target = "job")
    JobApplicationSeekerResponse toSeekerResponse(JobApplication jobApplication);
}
