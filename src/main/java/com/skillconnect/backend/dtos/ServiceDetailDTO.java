package com.skillconnect.backend.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ServiceDetailDTO {
    private Long id;
    private String title;
    private String description;
    private String category;
    private List<String> imageUrls;
    private LocalDateTime createdAt;

    private Long userId;
    private String username;

    private Double userAverageRating;
    private Integer totalReviews;
}