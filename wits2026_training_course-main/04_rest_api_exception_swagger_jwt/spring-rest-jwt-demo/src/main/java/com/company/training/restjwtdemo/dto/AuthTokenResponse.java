package com.company.training.restjwtdemo.dto;

public class AuthTokenResponse {

    private final String tokenType;
    private final String accessToken;
    private final long expiresInSeconds;

    public AuthTokenResponse(String tokenType, String accessToken, long expiresInSeconds) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }
}