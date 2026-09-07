package com.amin.jobportal.service;

import com.amin.jobportal.dto.request.RegisterRequest;
import com.amin.jobportal.dto.request.UpdateUserRequest;
import com.amin.jobportal.dto.response.UserResponse;
import com.amin.jobportal.entity.User;

public interface UserService {
    UserResponse register(RegisterRequest request);

    UserResponse getMe(User user);

    UserResponse update(Long Id, UpdateUserRequest request);

    void deleteAccount();
}
