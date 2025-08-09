package com.skillconnect.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private Service service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by")
    private User requestedBy;

    @Enumerated(EnumType.STRING)
    private BookingStatus status; // e.g., "pending", "confirmed", "cancelled"

    @CreationTimestamp
    private LocalDateTime requestedAt; // Timestamp when the booking was requested

    @Column(nullable = true)
    private LocalDateTime confirmedAt; // Timestamp when the booking was confirmed, if applicable

    @Column(nullable = true)
    private LocalDateTime cancelledAt; // Timestamp when the booking was cancelled, if applicable

    @UpdateTimestamp
    private LocalDateTime updatedAt; // Timestamp when the booking was last updated, if applicable
}
