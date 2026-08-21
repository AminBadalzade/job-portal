package com.amin.jobportal.controller;

import com.amin.jobportal.dto.request.LoginRequest;
import com.amin.jobportal.dto.response.TokenResponse;
import com.amin.jobportal.service.CustomerUserDetailsService;
import com.amin.jobportal.util.JWTUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
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

    public AuthController(CustomerUserDetailsService customerUserDetailsService, AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.customerUserDetailsService = customerUserDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> generateToken(@Valid @RequestBody LoginRequest loginRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        UserDetails userDetails = customerUserDetailsService.loadUserByUsername(loginRequest.getEmail());
        String accessToken = jwtUtil.generateToken(userDetails);

        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponse(accessToken));
    }
}
