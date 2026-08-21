package com.amin.jobportal.dto.request;

import com.amin.jobportal.enums.EmploymentType;
import com.amin.jobportal.enums.ExperienceLevel;
import com.amin.jobportal.enums.WorkType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CreateJobRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    @NotNull(message = "Experience level is required")
    private ExperienceLevel experienceLevel;

    @NotNull(message = "Work type is required")
    private WorkType workType;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    private String city;

    private String country;

    private String requirements;

    private String benefits;

    private LocalDateTime expiresAt;

    @NotNull(message = "Company ID is required")
    private Long companyId;
}
