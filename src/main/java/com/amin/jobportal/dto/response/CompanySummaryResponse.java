package com.amin.jobportal.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanySummaryResponse {
    private Long id;
    private String name;
    private String logoUrl;
    private String industry;
}
