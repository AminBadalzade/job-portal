package com.amin.jobportal.dto.response;

import com.amin.jobportal.enums.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class JobApplicationCompanyResponse {
    private Long id;

    private ApplicationStatus status;

    private LocalDateTime appliedAt;

    private UserSummaryResponse applicant;

    private ResumeResponse resume;
}
