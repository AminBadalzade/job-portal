package com.amin.jobportal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class UpdateUserRequest {
    @Length(max = 40)
    private String firstName;

    @Length(max = 40)
    private String lastName;
}
