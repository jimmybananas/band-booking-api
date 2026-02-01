package com.jspss.bandbooking.dto.requests.updates;

import com.jspss.bandbooking.entities.enums.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for updating the status of a booking.")
public record UpdateBookingStatus(

        @Schema(
                description = "Status of the booking to update."
        )
        @NotNull
        BookingStatus status
) {
}
