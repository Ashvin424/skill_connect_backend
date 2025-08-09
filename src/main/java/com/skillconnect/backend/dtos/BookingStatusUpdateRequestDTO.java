package com.skillconnect.backend.dtos;

import com.skillconnect.backend.models.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatusUpdateRequestDTO {

    private BookingStatus status;

    // Getter and Setter
}