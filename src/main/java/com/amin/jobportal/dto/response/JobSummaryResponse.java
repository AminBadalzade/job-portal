package com.amin.jobportal.dto.response;

import com.amin.jobportal.enums.EmploymentType;
import com.amin.jobportal.enums.ExperienceLevel;
import com.amin.jobportal.enums.WorkType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class JobSummaryResponse {
    private Long id;

    private String title;

    private String companyName;

    private String city;

    private String country;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private ExperienceLevel experienceLevel;

    private WorkType workType;

    private EmploymentType employmentType;

    private LocalDateTime createdAt;
}
