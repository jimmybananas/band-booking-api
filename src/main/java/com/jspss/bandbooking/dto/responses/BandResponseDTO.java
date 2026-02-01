package com.jspss.bandbooking.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Response body for Band.")
public record BandResponseDTO(

        @Schema(
                description = "Id of band.", example = "23"
        )
        Long id,

        @Schema(
                description = "Band name.", example = "The Chocolate Stuff Band"
        )
        String name,

        @Schema(
                description = "List of musicians IDs that belong to the band."
        )
        List<Long> musicianIds,

        @Schema(
                description = "List of music style IDs that belong to the band."
        )
        List<Long> musicStyleIds,

        @Schema(
                description = "List of instrument IDs that belong to the band."
        )
        List<Long> requiredInstrumentIds
) {
}
