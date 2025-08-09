package com.skillconnect.backend.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class RatingUpdateDTO {
    @Min(0)
    @Max(5)
    private int ratingValue;
    private String comment;

    public RatingUpdateDTO() {
    }

    public RatingUpdateDTO(int ratingValue, String comment) {
        this.ratingValue = ratingValue;
        this.comment = comment;
    }

    public int getRatingValue() {
        return ratingValue;
    }
    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

}
