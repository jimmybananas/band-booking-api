package com.jspss.bandbooking.dto.requests.create;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for the creation of an instrument.")
public record CreateInstrumentRequest(

        @Schema(
                description = "Name of the instrument."
        )
        @NotNull
        String name
) {
}
