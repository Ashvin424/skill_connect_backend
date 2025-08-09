package com.skillconnect.backend.service;

import com.skillconnect.backend.dtos.RatingRequestDTO;
import com.skillconnect.backend.dtos.RatingUpdateDTO;
import com.skillconnect.backend.models.Rating;
import com.skillconnect.backend.models.User;
import com.skillconnect.backend.repository.RatingRepository;
import com.skillconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    //TODO: Add pagination if require for get rating for user and in services also.......

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all ratings for test purposes
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    // Add a new rating
    public Rating addRating(RatingRequestDTO rating, UserDetails userDetails){
        User reviewer = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Reviewer user not found"));

        User reviewee = userRepository.findById(rating.getRevieweeId())
                .orElseThrow(() -> new RuntimeException("Reviewee not found"));

        if (reviewer.getId().equals(reviewee.getId())){
            throw new RuntimeException("You cannot rate yourself");
        }

        if (ratingRepository.existsByReviewerAndReviewee(reviewer, reviewee)) {
            throw new RuntimeException("You have already rated this user");
        }
        Rating userRating = new Rating();

        userRating.setReviewer(reviewer);
        userRating.setReviewee(reviewee);
        userRating.setRatingValue(rating.getRatingValue());
        userRating.setComment(rating.getComment());
        return ratingRepository.save(userRating);
    }

    //Get rating for a specific user
    public Page<Rating> getRatingsForUser(Long userId, Pageable pageable){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ratingRepository.findAllByReviewee_Id(userId, pageable);
    }

    // Get average rating for a specific user
    public double getAverageRatingForUser(Long userId) {
        return Optional.ofNullable(ratingRepository.findAverageRatingByUserId(userId)).orElse(0.0);
    }

    // update rating
    public Rating updateRating(Long ratingId, RatingUpdateDTO newRating, UserDetails userDetails){
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Rating existingRating = ratingRepository.findById(ratingId)
                .orElseThrow(()-> new RuntimeException("Rating not found"));

        if (!existingRating.getReviewer().getId().equals(user.getId())) {
            throw new RuntimeException("You can only update your own rating");
        }

        if (newRating.getRatingValue() < 1 || newRating.getRatingValue() > 5) {
            throw new RuntimeException("Rating value must be between 1 and 5");
        }

        existingRating.setRatingValue(newRating.getRatingValue());
        if (newRating.getComment() != null && !newRating.getComment().isBlank()) {
            existingRating.setComment(newRating.getComment().trim());
        }

        return ratingRepository.save(existingRating);
    }

    // delete rating
    public void deleteRating(Long ratingId, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Rating existingRating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Rating not found"));

        if (!existingRating.getReviewer().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own rating");
        }

        ratingRepository.delete(existingRating);
    }
}


//deleteRating() (optional)	For moderation or user control
