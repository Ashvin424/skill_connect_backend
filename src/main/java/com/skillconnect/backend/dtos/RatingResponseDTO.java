package com.skillconnect.backend.dtos;

import lombok.Data;

@Data
public class RatingResponseDTO {
    private Long id;
    private int ratingValue;
    private String comment;
    private Long reviewerId;
    private String reviewerName;
}
