package com.jspss.bandbooking.dto.summaries;

import com.jspss.bandbooking.entities.enums.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;
import java.util.List;

@Schema(description = "Summary DTO for bookings.")
public record BookingSummaryDTO(

        @Schema(description = "Booking ID", example = "23")
        Long id,

        @Schema(description = "Client ID", example = "44")
        Long clientId,

        @Schema(description = "Client name.", example = "David Karpinski")
        String clientName,

        @Schema(description = "Band ID", example = "69")
        Long bandId,

        @Schema(description = "Band name.", example = "DeVo")
        String bandName,

        @Schema(description = "Status of the booking.", example = "Pending")
        BookingStatus status,

        @Schema(description = "Time booking starts.", example = "2026-03-15T21:00:00Z")
        ZonedDateTime start,

        @Schema(description = "Time booking ends.", example = "2026-03-15T23:00:00Z")
        ZonedDateTime end
) {
}
