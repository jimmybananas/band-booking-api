package com.jspss.bandbooking.dto.requests.updates;

import com.jspss.bandbooking.entities.enums.BookingStatus;

public record UpdateBookingStatus(
        Long bookingId,
        BookingStatus status
) {
}
