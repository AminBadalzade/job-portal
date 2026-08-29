package com.amin.jobportal.dto.response;

import com.amin.jobportal.enums.JoinRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyJoinRequestResponse {

    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private JoinRequestStatus status;
    private LocalDateTime createdAt;
}
