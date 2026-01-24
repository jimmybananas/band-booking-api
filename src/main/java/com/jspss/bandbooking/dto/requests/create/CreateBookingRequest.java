package com.jspss.bandbooking.dto.requests.create;

import com.jspss.bandbooking.entities.enums.BookingStatus;

import java.time.ZonedDateTime;

public record CreateBookingRequest(
        Long id,
        Long clientId,
        Long bandId,
        ZonedDateTime start,
        ZonedDateTime end,
        String city,
        String state,
        BookingStatus status
) {
}
