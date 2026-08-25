package com.amin.jobportal.dto.request;

import com.amin.jobportal.enums.EmploymentType;
import com.amin.jobportal.enums.ExperienceLevel;
import com.amin.jobportal.enums.WorkType;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class JobSearchRequest {
    @Size(max = 100)
    private String title;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String country;

    private ExperienceLevel experienceLevel;

    private WorkType workType;

    private EmploymentType employmentType;

    @PositiveOrZero
    private BigDecimal salaryMin;

    @PositiveOrZero
    private BigDecimal salaryMax;
}
