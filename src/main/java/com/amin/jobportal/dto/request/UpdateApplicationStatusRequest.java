package com.amin.jobportal.dto.request;

import com.amin.jobportal.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateApplicationStatusRequest {
    @NotNull
    private ApplicationStatus status;
}
