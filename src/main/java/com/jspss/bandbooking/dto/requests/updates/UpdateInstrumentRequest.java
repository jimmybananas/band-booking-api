package com.jspss.bandbooking.dto.requests.updates;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for updating an instrument.")
public record UpdateInstrumentRequest(

        @Schema(
                description = "Name of the instrument.", example = "Slide guitar."
        )
        @NotNull
        String name
) {
}
