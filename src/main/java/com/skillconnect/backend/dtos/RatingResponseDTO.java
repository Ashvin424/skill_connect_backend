package com.skillconnect.backend.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RatingResponseDTO {
    private Long id;
    private int ratingValue;
    private String comment;
    private Long reviewerId;
    private String reviewerName;
    private LocalDateTime createdAt;
    private String reviewerProfileImageUrl;
}
