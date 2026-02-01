package com.jspss.bandbooking.dto.responses;

import com.jspss.bandbooking.dto.summaries.MusicianSummaryDTO;
import com.jspss.bandbooking.entities.enums.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.List;

@Schema(description = "Response body for a Booking.")
public record BookingResponseDTO(

        @Schema(
                description = "Booking ID", example = "1"
        )
        Long id,

        @Schema(
                description = "Name of band for the booking.", example = "Tower of Power"
        )
        String bandName,

        @Schema(
                description = "Band ID for the booking.", example = "12"
        )
        Long bandId,

        @Schema(
                description = "Name of the client."
        )
        String clientName,

        @Schema(
                description = "Client ID", example = "45"
        )
        Long clientId,

        @Schema(
                description = "List of musician summaries"
        )
        List<MusicianSummaryDTO> musicians,

        @Schema(
                description = "Gig start time.", example = "2026-03-15T21:00:00Z"
        )
        ZonedDateTime gigStarts,

        @Schema(
                description = "Booking end time.", example = "2026-03-15T21:00:00Z"
        )
        ZonedDateTime gigEnds,

        @Schema(
                description = "City of the booking.", example = "New York City"
        )
        String city,

        @Schema(
                description = "State of the booking", example = "New York"
        )
        String state,

        @Schema(
                description = "Status of the booking.", example = "BookingStatus.Pending"
        )
        BookingStatus status
) {


}
