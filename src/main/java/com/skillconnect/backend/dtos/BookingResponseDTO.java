package com.skillconnect.backend.dtos;

import com.skillconnect.backend.models.BookingStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingResponseDTO {
    private Long id;
    private Long userId;
    private Long serviceId;
    private String message;
    private BookingStatus status; // "PENDING", "APPROVED", etc.
    private LocalDateTime requestedAt; // Timestamp when the booking was requested
    private LocalDateTime confirmedAt; // Timestamp when the booking was confirmed, if applicable
    private LocalDateTime cancelledAt;
    private LocalDateTime updatedAt;
}