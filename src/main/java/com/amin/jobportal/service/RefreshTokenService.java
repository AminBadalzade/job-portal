package com.amin.jobportal.service;

import com.amin.jobportal.entity.RefreshToken;
import com.amin.jobportal.entity.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);

    RefreshToken verifyRefreshToken(String token);

    void revokeToken(String token);
}
