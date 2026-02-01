package com.jspss.bandbooking.dto.requests.updates;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Request body for updating a band.")
public record UpdateBandRequest(

        @Schema(
                description = "The name of the band."
        )
        @NotNull
        String name,

        @Schema(
                description = "List of music style IDs.", example = "[1,3,5]"
        )
        @NotNull
        List<Long> musicStyleIdList,

        @Schema(
                description = "List of musician IDs.", example = "[1,3,5]"
        )
        @NotNull
        List<Long> musicianIdList,

        @Schema(
                description = "List of instrument IDs.", example = "[1,3,5]"
        )
        @NotNull
        List<Long> requiredInstrumentsIdList
) {
}
