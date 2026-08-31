package com.amin.jobportal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResumeResponse {

    private Long id;

    private String fileName;

    private String downloadUrl;

    private LocalDateTime uploadedAt;
}