package com.skillconnect.backend.dtos;

public class RatingRequestDTO {
    private Long reviewerId;
    private Long revieweeId;
    private int ratingValue;
    private String comment;

    public RatingRequestDTO() {
    }
    public RatingRequestDTO(Long reviewerId, Long revieweeId, int ratingValue, String comment) {
        this.reviewerId = reviewerId;
        this.revieweeId = revieweeId;
        this.ratingValue = ratingValue;
        this.comment = comment;
    }

    public Long getReviewerId() {
        return reviewerId;
    }
    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }
    public Long getRevieweeId() {
        return revieweeId;
    }
    public void setRevieweeId(Long revieweeId) {
        this.revieweeId = revieweeId;
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
