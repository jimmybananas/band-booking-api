package com.jspss.bandbooking.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body for Instrument DTO")
public record InstrumentResponseDTO(

        @Schema(
                description = "Instrument ID", example = "23"
        )
        Long id,
        @Schema(
                description = "Instrument name"
        )
        String name
) {
}
