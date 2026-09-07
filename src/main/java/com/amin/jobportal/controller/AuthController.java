package com.amin.jobportal.controller;

import com.amin.jobportal.dto.request.LoginRequest;
import com.amin.jobportal.dto.request.RefreshTokenRequest;
import com.amin.jobportal.dto.response.AuthResponse;
import com.amin.jobportal.entity.RefreshToken;
import com.amin.jobportal.entity.User;
import com.amin.jobportal.service.CustomerUserDetailsService;
import com.amin.jobportal.service.RefreshTokenService;
import com.amin.jobportal.util.JWTUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final CustomerUserDetailsService customerUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;


    public AuthController(CustomerUserDetailsService customerUserDetailsService, AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.customerUserDetailsService = customerUserDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> generateToken(@Valid @RequestBody LoginRequest loginRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        User user = (User) customerUserDetailsService.loadUserByUsername(loginRequest.getEmail());
        String accessToken = jwtUtil.generateToken(user);
        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        AuthResponse response = new AuthResponse(
                accessToken,
                refreshToken.getToken()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestBody RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenService.verifyRefreshToken(
                        request.getRefreshToken()
                );

        User user = refreshToken.getUser();

        String accessToken = jwtUtil.generateToken(user);

        return ResponseEntity.ok(
                new AuthResponse(
                        accessToken,
                        refreshToken.getToken()
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody RefreshTokenRequest request) {

        refreshTokenService.revokeToken(request.getRefreshToken());

        return ResponseEntity.noContent().build();
    }

}
