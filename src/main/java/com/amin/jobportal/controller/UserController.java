package com.amin.jobportal.controller;

import com.amin.jobportal.dto.response.UserResponse;
import com.amin.jobportal.entity.User;
import com.amin.jobportal.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public UserResponse getMe(@AuthenticationPrincipal User user){
        return userService.getMe(user);
    }
}
