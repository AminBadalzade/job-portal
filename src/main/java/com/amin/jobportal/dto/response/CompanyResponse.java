package com.amin.jobportal.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyResponse {
    private Long id;

    private String name;

    private String description;

    private String industry;

    private String logoUrl;
}
