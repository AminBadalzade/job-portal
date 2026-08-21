package com.amin.jobportal.dto.response;

import com.amin.jobportal.enums.EmploymentType;
import com.amin.jobportal.enums.ExperienceLevel;
import com.amin.jobportal.enums.JobStatus;
import com.amin.jobportal.enums.WorkType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class JobResponse {
    private Long id;

    private String title;

    private String description;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private ExperienceLevel experienceLevel;

    private WorkType workType;

    private EmploymentType employmentType;

    private String city;

    private String country;

    private String requirements;

    private String benefits;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime expiresAt;

    private JobStatus status;

    private CompanyResponse company;
}
