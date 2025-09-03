package com.skillconnect.backend.controller;

import com.skillconnect.backend.dtos.BookingRequestDTO;
import com.skillconnect.backend.dtos.BookingResponseDTO;
import com.skillconnect.backend.dtos.BookingStatusUpdateRequestDTO;
import com.skillconnect.backend.models.Booking;
import com.skillconnect.backend.models.Service;
import com.skillconnect.backend.models.User;
import com.skillconnect.backend.service.BookingService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    //Get Booking for Requested by User
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id);
        if (booking == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    // Get all bookings for a requested by user
    @GetMapping("/requested-by/{userId}")
    public ResponseEntity<Page<BookingResponseDTO>> getBookingsByRequester(@PathVariable Long userId,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        Page<BookingResponseDTO> bookings = bookingService.getBookingsRequestedByUser(userId, PageRequest.of(page, size));
        if (bookings.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/service-provider/{providerId}")
    public ResponseEntity<Page<BookingResponseDTO>> getBookingsByServiceProviderId(@PathVariable Long providerId,
                                                                                   @RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "10") int size) {
        Page<BookingResponseDTO> bookings = bookingService.getBookingsForServiceProvider(providerId, PageRequest.of(page, size));
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDTO bookingRequest,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Booking booking = bookingService.createBooking(userDetails, bookingRequest);
            return new ResponseEntity<>(booking, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            // Handle specific business logic exceptions
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (RuntimeException e) {
            // Catch other potential runtime errors
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBookingStatus(
            @PathVariable Long id,
            @RequestBody @Valid BookingStatusUpdateRequestDTO bookingStatusRequest)
    {

        if (bookingStatusRequest.getStatus() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Booking updatedBooking = bookingService.updateBookingStatus(id, bookingStatusRequest.getStatus());
            return ResponseEntity.ok(updatedBooking);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long id,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Booking cancelledBooking = bookingService.cancelBooking(id, userDetails);
            return new ResponseEntity<>(cancelledBooking, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
