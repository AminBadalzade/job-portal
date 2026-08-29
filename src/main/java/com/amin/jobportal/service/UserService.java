package com.amin.jobportal.service;

import com.amin.jobportal.dto.request.RegisterRequest;
import com.amin.jobportal.dto.request.UpdateUserRequest;
import com.amin.jobportal.dto.response.UserResponse;

public interface UserService {
    UserResponse register(RegisterRequest request);

    UserResponse getCurrentUser();

    UserResponse update(Long Id, UpdateUserRequest request);

    void deleteAccount();
}
