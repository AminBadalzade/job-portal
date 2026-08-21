package com.amin.jobportal.dto.request;

import jakarta.validation.constraints.NotNull;

public class JobApplicationRequest {
    @NotNull(message = "Job ID is required")
    private Long jobId;

    @NotNull(message = "Resume ID is required")
    private Long resumeId;
}
