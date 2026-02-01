package com.jspss.bandbooking.dto.requests.create;

import com.jspss.bandbooking.entities.enums.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

@Schema(description = "Request body for the creation of a booking.")
public record CreateBookingRequest(

        @Schema(
                description = "The id of the client requesting the booking.", example = "1"
        )
        @NotNull
        Long clientId,

        @Schema(
                description = "ID of the band assigned to the booking.", example = "2"
        )
        @NotNull
        Long bandId,

        @Schema(
                description = "Start time of the booking.", example = "2026-03-15T19:00:00Z"
        )
        @NotNull
        ZonedDateTime start,

        @Schema(
                description = "End time of the booking.", example = "2026-03-15T21:00:00Z"
        )
        @NotNull
        ZonedDateTime end,

        @Schema(
                description = "City of the booking.", example = "Colorado Springs"
        )
        @NotNull
        String city,

        @Schema(
                description = "State of the booking.", example = "Colorado"
        )
        String state
) {
}
