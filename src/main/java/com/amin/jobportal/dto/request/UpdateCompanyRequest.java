package com.amin.jobportal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class UpdateCompanyRequest {
    @Length(max = 150)
    private String name;

    @Length(max = 3000)
    private String description;

    @Length(max = 100)
    private String industry;
}
