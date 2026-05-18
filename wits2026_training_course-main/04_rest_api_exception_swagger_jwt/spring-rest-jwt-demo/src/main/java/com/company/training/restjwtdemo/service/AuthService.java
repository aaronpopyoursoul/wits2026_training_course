package com.company.training.restjwtdemo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.company.training.restjwtdemo.dto.AuthTokenResponse;
import com.company.training.restjwtdemo.exception.BusinessException;
import com.company.training.restjwtdemo.security.JwtTokenProvider;

@Service
public class AuthService {

    @Value("${training.auth.username}")
    private String configuredUsername;

    @Value("${training.auth.password}")
    private String configuredPassword;

    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthTokenResponse login(String username, String password) {
        if (!configuredUsername.equals(username) || !configuredPassword.equals(password)) {
            throw new BusinessException("AUTHENTICATION_FAILED", "Username or password is incorrect");
        }

        String token = jwtTokenProvider.createToken(username);
        return new AuthTokenResponse("Bearer", token, jwtTokenProvider.getExpirationSeconds());
    }
}