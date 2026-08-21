package com.amin.jobportal.dto.request;

import com.amin.jobportal.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank
    @Length(max = 40)
    private String firstName;

    @NotBlank
    @Length(max = 40)
    private String lastName;


    @NotBlank
    @Email
    @Length(max = 150)
    private String email;

    @NotBlank
    @Length(min = 8, max = 50)
    private String password;

    @NotNull
    private Role role;
}
