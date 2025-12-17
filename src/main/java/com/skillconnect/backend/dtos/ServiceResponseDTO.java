package com.skillconnect.backend.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ServiceResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String category;
    private List<String> imageUrls;
    private Long userId;
    private String username;
    private String providerMode;
    private Boolean isActive;

    private Double userRating;
    private String userProfileImageUrl;
}
