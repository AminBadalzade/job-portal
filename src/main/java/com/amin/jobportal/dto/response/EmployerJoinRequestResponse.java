package com.amin.jobportal.dto.response;

import com.amin.jobportal.enums.JoinRequestStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EmployerJoinRequestResponse {
    private Long id;
    private Long companyId;
    private String companyName;
    private JoinRequestStatus status;
    private LocalDateTime createdAt;
}
