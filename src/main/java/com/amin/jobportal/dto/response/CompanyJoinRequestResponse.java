package com.amin.jobportal.dto.response;

import com.amin.jobportal.enums.JoinRequestStatus;

import java.time.LocalDateTime;

public class CompanyJoinRequestResponse {

    private Long id;
    private Long userId;
    private String username;
    private JoinRequestStatus status;
    private LocalDateTime createdAt;
}
