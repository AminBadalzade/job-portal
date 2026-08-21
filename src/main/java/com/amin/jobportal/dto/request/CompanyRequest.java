package com.amin.jobportal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequest {
    @NotBlank
    @Length(max = 150)
    private String name;

    @NotBlank
    @Length(max = 3000)
    private String description;

    @NotBlank
    @Length(max = 100)
    private String industry;

    private String logoUrl;

}