package com.skillconnect.backend.controller;

import com.skillconnect.backend.dtos.RatingRequestDTO;
import com.skillconnect.backend.dtos.RatingResponseDTO;
import com.skillconnect.backend.dtos.RatingUpdateDTO;
import com.skillconnect.backend.dtos.ServiceResponseDTO;
import com.skillconnect.backend.models.Rating;
import com.skillconnect.backend.models.Service;
import com.skillconnect.backend.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ratings")
public class RatingController {
    @Autowired
    private RatingService ratingService;

    //Get All Ratings
    @GetMapping("/all/ratings")
    public ResponseEntity<List<RatingResponseDTO>> getAllRatings(){
        List<Rating> allRatings = ratingService.getAllRatings();
        if (allRatings.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        // Convert List<Rating> to List<RatingResponseDTO>
        List<RatingResponseDTO> dtos = allRatings.stream()
                .map(this::mapToDTO)
                .toList();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<RatingResponseDTO>> getRatingForUser(@PathVariable Long userId,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size){
        Page<Rating> ratings = ratingService.getRatingsForUser(userId, PageRequest.of(page, size));

        if (ratings.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        Page<RatingResponseDTO> dtos = ratings.map(this::mapToDTO);
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/average/{userId}")
    public ResponseEntity<?> getAverageRatingForUser(@PathVariable Long userId){
        double avgRating = ratingService.getAverageRatingForUser(userId);
        if (avgRating == 0.0) {
            return new ResponseEntity<>("No ratings found for this user", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(avgRating, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addRating(@Valid @RequestBody RatingRequestDTO rating,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Rating savedRating = ratingService.addRating(rating, userDetails);
            RatingResponseDTO responseDTO = mapToDTO(savedRating);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{ratingId}")
    public ResponseEntity<?> updateRating(@PathVariable Long ratingId,
                                          @RequestBody RatingUpdateDTO newRating,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Rating updatedRating = ratingService.updateRating(ratingId, newRating, userDetails);
            RatingResponseDTO responseDTO = mapToDTO(updatedRating);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{ratingId}")
    public ResponseEntity<?> deleteRating(@PathVariable Long ratingId, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            ratingService.deleteRating(ratingId, userDetails);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private RatingResponseDTO mapToDTO(Rating rating) {
        RatingResponseDTO dto = new RatingResponseDTO();
        dto.setId(rating.getId());
        dto.setRatingValue(rating.getRatingValue());
        dto.setComment(rating.getComment());
        dto.setReviewerId(rating.getReviewer().getId());
        dto.setReviewerName(rating.getReviewer().getName());
        return dto;
    }
}
