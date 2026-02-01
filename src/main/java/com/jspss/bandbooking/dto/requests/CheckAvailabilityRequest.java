package com.jspss.bandbooking.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

@Schema(description = "Request body for checking band availability.")
public record CheckAvailabilityRequest(

        @Schema(description = "Band ID", example = "45")
        Long bandId,
        @Schema(description = "Booking start time.", example = "2026-03-15T09:00:00Z")
        ZonedDateTime start,

        @Schema(description = "Booking end time.", example = "2026-03-15T11:00:00Z")
        ZonedDateTime end
) {
}
