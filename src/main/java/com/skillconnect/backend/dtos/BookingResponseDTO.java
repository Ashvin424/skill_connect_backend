package com.skillconnect.backend.dtos;

import com.skillconnect.backend.models.BookingStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingResponseDTO {
    private Long id;
    private Long userId;
    private Long serviceId;
    private Long serviceProviderId;
    private String serviceTitle;
    private String requestedByName; // Name of the user who requested the booking
    private String serviceProviderName; // Name of the service provider
    private BookingStatus status; // "PENDING", "APPROVED", etc.
    private LocalDateTime requestedAt; // Timestamp when the booking was requested
    private LocalDateTime confirmedAt; // Timestamp when the booking was confirmed, if applicable
    private LocalDateTime cancelledAt;
    private LocalDateTime updatedAt;

    public BookingResponseDTO(Long id, Long userId, Long serviceId, Long serviceProviderId, String serviceTitle, String requestedByName,String serviceProviderName ,BookingStatus status, LocalDateTime requestedAt, LocalDateTime confirmedAt, LocalDateTime cancelledAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.serviceId = serviceId;
        this.serviceProviderId = serviceProviderId;
        this.serviceTitle = serviceTitle;
        this.requestedByName = requestedByName;
        this.serviceProviderName = serviceProviderName;
        this.status = status;
        this.requestedAt = requestedAt;
        this.confirmedAt = confirmedAt;
        this.cancelledAt = cancelledAt;
        this.updatedAt = updatedAt;
    }
}