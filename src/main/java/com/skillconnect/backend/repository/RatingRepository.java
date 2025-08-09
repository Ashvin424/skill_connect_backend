package com.skillconnect.backend.repository;

import com.skillconnect.backend.models.Rating;
import com.skillconnect.backend.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    boolean existsByReviewerAndReviewee(User ratedBy, User ratedUser);

    List<Rating> findAllByReviewee(User user);
    // This interface will automatically inherit methods for CRUD operations
    // and can be extended with custom query methods if needed.

    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.reviewee.id = :userId")
    Double findAverageRatingByUserId(@Param("userId") Long userId);
    Page<Rating> findAllByReviewee_Id(Long userId, Pageable pageable);


    int countByReviewee_Id(Long id);
}
