package com.jspss.bandbooking.dto.summaries;

import com.jspss.bandbooking.entities.enums.BookingStatus;

import java.time.ZonedDateTime;
import java.util.List;

public record BookingSummaryDTO(
        Long id,
        Long clientId,
        String clientName,
        Long bandId,
        String bandName,
        BookingStatus status,
        ZonedDateTime start,
        ZonedDateTime end
) {
}
