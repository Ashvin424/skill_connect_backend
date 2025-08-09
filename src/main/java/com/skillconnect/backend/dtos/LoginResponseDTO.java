package com.skillconnect.backend.dtos;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken; // Optional, if you want to include a refresh token
    private String email;
    private String fullName;
    private Long userId;

    public LoginResponseDTO() {
    }
    public LoginResponseDTO(String accessToken, String email, String fullName, Long userId) {
        this.accessToken = accessToken;
        this.email = email;
        this.fullName = fullName;
        this.userId = userId;
    }

    public LoginResponseDTO(String newAccessToken, String refreshToken) {
    }


    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
