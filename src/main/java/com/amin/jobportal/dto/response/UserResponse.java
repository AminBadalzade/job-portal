package com.amin.jobportal.dto.response;

import com.amin.jobportal.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Role role;

    private CompanyResponse company;
}
