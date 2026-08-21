package com.amin.jobportal.dto.request;

import com.amin.jobportal.enums.EmploymentType;
import com.amin.jobportal.enums.ExperienceLevel;
import com.amin.jobportal.enums.WorkType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateJobRequest {
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

    @FutureOrPresent
    private LocalDateTime expiresAt;
}
